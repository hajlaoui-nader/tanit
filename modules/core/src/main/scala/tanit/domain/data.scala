package tanit.domain

import io.estatico.newtype.macros._

object data {

  @newtype case class UserId(value: String)

  @newtype case class FirstName(value: String)

  @newtype case class LastName(value: String)

  case class User(userId: UserId, firstName: FirstName, lastName: LastName)
}
