package tanit

import tanit.infra.config.KafkaConfig
import tanit.infra.streams.UserStream

import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.slf4j.Slf4jLogger
object Main extends IOApp.Simple {
  implicit val loggerr = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val kafkaConfig = KafkaConfig(List("localhost:9092"), "test")

    for {
      _ <- new UserStream(kafkaConfig, "users").mkStream.compile.drain
    } yield ()
  }
}
