package tanit.domain

trait UserService[F[_]] {
  def create(user: User): F[User]
  def find(id: UserId): F[Option[User]]
}
