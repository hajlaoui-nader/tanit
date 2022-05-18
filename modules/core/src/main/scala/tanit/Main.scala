package tanit

import scala.concurrent.duration._

import tanit.infra.config.KafkaConfig
import tanit.infra.streams.{UserProducer, UserStream}

import cats.effect.{ IO, IOApp }
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val loggerr = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val kafkaConfig     = KafkaConfig(List("localhost:9092"), "tanit_consumer")
    val usersTopic      = "users"
    val kafkaProperties = Map.empty[String, String]
    val producer        = new UserProducer[IO](kafkaConfig, usersTopic)

    val producerEveryNSeconds = fs2.Stream.awakeEvery[IO](5.seconds).flatMap(_ => producer.produce)

    for {
      _ <- new UserStream[IO](kafkaConfig, usersTopic, kafkaProperties).mkStream
        .concurrently(producerEveryNSeconds)
        .compile
        .drain
      // _ <- new UserStream[IO](kafkaConfig, usersTopic, kafkaProperties).mkStream.compile.drain.start
      // _ <- producerEveryNSeconds.compile.drain
    } yield ()
  }

}
