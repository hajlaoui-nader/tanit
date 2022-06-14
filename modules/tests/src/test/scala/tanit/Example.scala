package tanit

import cats.effect._
import weaver._

object Example extends SimpleIOSuite {
  val uuid = IO(java.util.UUID.randomUUID())

  test("UUID is random") {
    for {
      u1 <- uuid
      u2 <- uuid
    } yield expect(u1 != u2)
  }

  test("UUID always contains 36 characters") {
    uuid.map(u => expect(u.toString.size == 36))
  }
}
