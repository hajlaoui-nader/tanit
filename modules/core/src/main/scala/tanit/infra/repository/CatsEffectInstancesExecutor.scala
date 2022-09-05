package tanit.infra.repository
import cats.effect.kernel.Async
import cats.{ Functor => CatsFunctor }
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.cats.effect

object CatsEffectInstancesExecutor {

  implicit def catsFunctor[F[_]: CatsFunctor]: Functor[F] = new Functor[F] {
    override def map[A, B](fa: F[A])(f: A => B): F[B] = CatsFunctor[F].map(fa)(f)
  }

  implicit def asyncExecutor[F[_]: Async]: Executor[F] = new effect.CatsEffectExecutor[F] {
    override def exec(client: HttpClient, request: ElasticRequest): F[HttpResponse] =
      Async[F].async_[HttpResponse](k => client.send(request, k))
  }

}
