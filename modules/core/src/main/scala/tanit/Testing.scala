package tanit

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext
import scala.util.Random

import cats.effect.{IO, IOApp}
import fs2.Stream

object Testing extends IOApp.Simple {

  case class User(id: Int, firstName: String, lastName: String)

  val nonBlocking = ExecutionContext.fromExecutor(
    Executors.newCachedThreadPool()
  )

  val nader   = User(1, "Nader", "Sadeghi")
  val maurine = User(2, "Maurine", "ighetti")

  val naderStream   = Stream(nader).repeat.evalMap(IO.println)
  val maurineStream = Stream(maurine).repeat.evalMap(IO.println)

  def doSomeRiskyStuff(user: User): IO[Unit] =
    if (Random.nextBoolean())
      IO.println(s"${user.firstName} is doing risky stuff")
    else throw new RuntimeException("oh no")

  val riskyStream = Stream(nader, maurine).repeat.evalMap(doSomeRiskyStuff)
  val riskyStreamHandled = Stream(nader, maurine).repeat
    .evalMap(doSomeRiskyStuff)
    .handleErrorWith(_ => Stream(User(11, "error fisrt name", "error last name")).evalMap(IO.println))

  override def run: IO[Unit] = {
    for {
      _ <- riskyStreamHandled.compile.drain
    } yield ()
  }
}
