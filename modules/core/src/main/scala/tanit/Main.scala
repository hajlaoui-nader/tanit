package tanit

import scala.concurrent.duration._

import tanit.domain.UserService
import tanit.infra.api._
import tanit.infra.config._
import tanit.infra.repository.OpensearchUsers
import tanit.infra.streams.{ UserProducer, UserStream }

import cats.effect.kernel.Resource
import cats.effect.{ IO, IOApp }
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ ElasticClient, ElasticProperties }
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val kafkaConfig      = KafkaConfig(List("localhost:9092"), "tanit_consumer", 500)
    val opensearchConfig = OpensearchConfig("http://localhost", 9200)
    val usersTopic       = "users"
    val kafkaProperties  = Map.empty[String, String]
    val producer         = new UserProducer[IO](kafkaConfig, usersTopic)

    val producerEveryNSeconds = fs2.Stream.awakeEvery[IO](5.seconds).flatMap(_ => producer.produce)

    val healthRoute = new HealthRoute[IO]().routes
    val mkServer    = new WebServer[IO](healthRoute).mkServer

    // TODO [NH] move to seperate classs
    def opensearchClientResource: Resource[IO, ElasticClient] =
      Resource.make {
        val props = ElasticProperties(s"${opensearchConfig.host}:${opensearchConfig.port}")
        IO.delay {
          logger.info("opening opensearch connection")
          ElasticClient(JavaClient(props))
        }
      }(c => IO.delay { c.close() })

    logger.info("starting up") *> fs2.Stream
      .resource(mkServer)
      .flatMap { _ =>
        fs2.Stream
          .resource(opensearchClientResource)
          .flatMap { opensearchClient =>
            val userService = new UserService(new OpensearchUsers[IO](opensearchClient))
            new UserStream[IO](kafkaConfig, usersTopic, kafkaProperties)(userService).mkStream
              .concurrently(producerEveryNSeconds)
          }
      }
      .compile
      .drain
  }
}
