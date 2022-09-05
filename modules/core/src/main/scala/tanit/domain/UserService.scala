package tanit.domain

import tanit.domain.data._

trait Users[F[_]] {
  def create(user: User): F[User]
  def find(id: UserId): F[Option[User]]
}

// TODO [nh] implement me + add repository
final class UserService[F[_]](users: Users[F]) {
  def create(user: User): F[User] = users.create(user)

  def find(id: UserId): F[Option[User]] = users.find(id)

}
