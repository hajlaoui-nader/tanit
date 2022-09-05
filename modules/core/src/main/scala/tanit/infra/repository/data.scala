package tanit.infra.repository

import tanit.domain

object data {
  case class User(userId: String, firstName: String, lastName: String)

  object User {
    def fromDomain(user: domain.data.User): User =
      User(user.userId.value, user.firstName.value, user.lastName.value)
  }

}
