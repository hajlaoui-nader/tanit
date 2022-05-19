package tanit

import scala.concurrent.duration._

import tanit.infra.config.KafkaConfig
import tanit.infra.streams.{ UserProducer, UserStream }
// import org.http4s.server.defaults.Banner

import cats.effect.{ IO, IOApp }
import org.typelevel.log4cats.slf4j.Slf4jLogger
// import org.http4s.ember.server.EmberServerBuilder

object Main extends IOApp.Simple {
  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val kafkaConfig     = KafkaConfig(List("localhost:9092"), "tanit_consumer")
    val usersTopic      = "users"
    val kafkaProperties = Map.empty[String, String]
    val producer        = new UserProducer[IO](kafkaConfig, usersTopic)

    val producerEveryNSeconds = fs2.Stream.awakeEvery[IO](5.seconds).flatMap(_ => producer.produce)

    for {
      // _ <- EmberServerBuilder
      //   .default[IO]
      //   .build
      // .evalTap(_ => logger.info(s"\n${Banner.mkString("\n")}\nHTTP Server started"))
      // .useForever
      _ <- new UserStream[IO](kafkaConfig, usersTopic, kafkaProperties).mkStream
        .concurrently(producerEveryNSeconds)
        .compile
        .drain
    } yield ()
  }

}
