package tanit

import scala.concurrent.duration._

import tanit.infra.config.KafkaConfig
import tanit.infra.streams.{ UserProducer, UserStream }

import cats.Monad
import cats.effect.{ IO, IOApp }
import com.comcast.ip4s.{Host, Port}
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.defaults.Banner
import org.http4s.server.middleware._
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val kafkaConfig     = KafkaConfig(List("localhost:9092"), "tanit_consumer")
    val usersTopic      = "users"
    val kafkaProperties = Map.empty[String, String]
    val producer        = new UserProducer[IO](kafkaConfig, usersTopic)

    val producerEveryNSeconds = fs2.Stream.awakeEvery[IO](5.seconds).flatMap(_ => producer.produce)

    val middleware: HttpRoutes[IO] => HttpRoutes[IO] = {
      { http: HttpRoutes[IO] =>
        AutoSlash(http)
      } andThen { http: HttpRoutes[IO] =>
        CORS(http)
      } andThen { http: HttpRoutes[IO] =>
        Timeout(60.seconds)(http)
      }
    }

    val loggers: HttpApp[IO] => HttpApp[IO] = {
      { http: HttpApp[IO] =>
        RequestLogger.httpApp(true, true)(http)
      } andThen { http: HttpApp[IO] =>
        ResponseLogger.httpApp(true, true)(http)
      }
    }

    val httpApp = loggers(middleware(new HealthRoutes[IO].routes).orNotFound)

    val mkServer = EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(Port.fromInt(8080).get)
      .withHttpApp(httpApp)
      .build
      .evalTap(_ => logger.info(s"\n${Banner.mkString("\n")}\nHTTP Server started"))

    fs2.Stream
      .resource(mkServer)
      .flatMap { _ =>
        new UserStream[IO](kafkaConfig, usersTopic, kafkaProperties).mkStream
          .concurrently(producerEveryNSeconds)
      }
      .compile
      .drain

    // for {
    //   _ <- new UserStream[IO](kafkaConfig, usersTopic, kafkaProperties).mkStream
    //     .concurrently(producerEveryNSeconds)
    //     .compile
    //     .drain
    // } yield ()

  }

  final case class HealthRoutes[F[_]: Monad]() extends Http4sDsl[F] {

    private val prefixPath = "/healthcheck"

    private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root =>
        Ok("hello check")
    }

    val routes: HttpRoutes[F] = Router(
      prefixPath -> httpRoutes
    )

  }

}
