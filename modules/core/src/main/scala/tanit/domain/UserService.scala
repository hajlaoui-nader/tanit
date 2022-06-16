package tanit.domain

import tanit.domain.data._
trait UserService[F[_]] {
  def create(user: User): F[User]
  def find(id: UserId): F[Option[User]]
}
