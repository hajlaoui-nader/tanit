package tanit

import scala.concurrent.duration._

import tanit.infra.api._
import tanit.infra.config.KafkaConfig
import tanit.infra.streams.{ UserProducer, UserStream }

import cats.effect.{ IO, IOApp }
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val kafkaConfig     = KafkaConfig(List("localhost:9092"), "tanit_consumer", 500)
    val usersTopic      = "users"
    val kafkaProperties = Map.empty[String, String]
    val producer        = new UserProducer[IO](kafkaConfig, usersTopic)

    val producerEveryNSeconds = fs2.Stream.awakeEvery[IO](5.seconds).flatMap(_ => producer.produce)

    val healthRoute = new HealthRoute[IO]().routes
    val mkServer    = new WebServer[IO](healthRoute).mkServer

    logger.info("starting up") *> fs2.Stream
      .resource(mkServer)
      .flatMap { _ =>
        new UserStream[IO](kafkaConfig, usersTopic, kafkaProperties).mkStream
          .concurrently(producerEveryNSeconds)
      }
      .compile
      .drain
  }
}
