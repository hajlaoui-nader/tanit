package tanit.infra.api

import cats.effect._
import weaver._
import org.http4s.Method._
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.syntax.literals._
// import io.circe._
// import io.circe.syntax._
// import org.http4s.circe._

// TODO [nh] tests
object HealthRouteSuite extends SimpleIOSuite {
  test("GET health status") {
    val req   = GET(uri"/healthcheck")
    val route = new HealthRoute[IO].routes

    route.run(req).value.flatMap {
      case Some(resp) =>
        IO { expect.same(resp.status, Status.Ok) }
      case None => IO.pure(failure("route nout found"))
    }
  }
}
