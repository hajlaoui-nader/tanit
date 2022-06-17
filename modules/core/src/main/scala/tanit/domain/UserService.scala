package tanit.domain

import tanit.domain.data._
trait Users[F[_]] {
  def create(user: User): F[User]
  def find(id: UserId): F[Option[User]]
}
