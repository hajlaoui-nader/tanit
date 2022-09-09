package tanit.domain

import weaver._
import cats.effect.IO

object UserServiceSuite extends SimpleIOSuite {
  test("save user") {
    IO {
      println("testing")
      expect.same(1, 1)
    }
  }
}
