package tanit.infra.api

import cats.effect._
import weaver._

// TODO [nh] tests
object HealthRouteSuite extends SimpleIOSuite {
  test("GET health status") {
    IO { expect.same(1, 1) }
  }
}
