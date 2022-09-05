package tanit

import cats.effect._
import cats.instances.list._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import com.sksamuel.elastic4s.circe._

import com.sksamuel.elastic4s.http._


import io.circe.generic.auto._
import com.sksamuel.elastic4s.requests.indexes.CreateIndexRequest
import com.sksamuel.elastic4s.requests.indexes.IndexRequest
import com.sksamuel.elastic4s.requests.searches.SearchRequest
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticProperties
import com.sksamuel.elastic4s.Response
import com.sksamuel.elastic4s.RequestFailure
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import com.sksamuel.elastic4s.RequestSuccess
import com.sksamuel.elastic4s.Executor
import com.sksamuel.elastic4s

case class ModernArtist(name2: String)

trait Program[F[_]] {
  import com.sksamuel.elastic4s.ElasticDsl._
  implicit val F: Sync[F]

  private def puts(s: Any): F[Unit] = F.delay { println(s) }

  val createArtists: CreateIndexRequest = createIndex("artists") 
  val indexModern: IndexRequest =
    indexInto("artists/modern").source(ModernArtist("L.S. Lowry")).refreshImmediately

  val searchModern: SearchRequest = search("artists").query("lowry")

  def client: Resource[F, ElasticClient] = Resource.make {
        val props = ElasticProperties(s"blabla")
        F.delay { ElasticClient(JavaClient(props)) }
      }(c => F.delay { c.close() })

      
  def report(res: Response[SearchResponse]): F[Unit] = res match {
    case RequestFailure(_, _, _, e) => puts(s"We failed: $e")
    case RequestSuccess(_, _, _, r) => for {
      _ <- r.to[ModernArtist].toList.map(puts(_)).sequence
      _ <- puts(s"There were ${r.totalHits} total hits")
    } yield ()
  }

  def program(implicit U: elastic4s.Functor[F], E: Executor[F]): F[Unit] = client use { c =>
    for {
      _ <- c.execute(createArtists)
      _ <- c.execute(indexModern)
      _ <- c.execute(searchModern) >>= report
    } yield ()
  }
}
object ArtistIndex extends IOApp with Program[IO] {
  import com.sksamuel.elastic4s.cats.effect.instances._
  val F: Sync[IO] = Sync[IO]

  def run(args: List[String]): IO[ExitCode] = program as ExitCode.Success
}