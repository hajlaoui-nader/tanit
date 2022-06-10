package tanit

import cats.effect.IOApp
import cats.effect.IO
import org.typelevel.log4cats._
import org.typelevel.log4cats.slf4j._

object TestLogger extends IOApp.Simple {

  val logger = LoggerFactory[IO].getLogger

  override def run: IO[Unit] = logger.error("wtf really") *> IO.println("end")
}
