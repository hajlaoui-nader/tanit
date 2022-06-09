package tanit

import cats.effect.IOApp
import cats.effect.IO
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

object TestLogger extends IOApp.Simple {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = Logger[IO].error("wtf really") *> IO.println("end")
}
