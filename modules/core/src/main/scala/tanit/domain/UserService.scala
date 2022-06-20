package tanit.domain

import tanit.domain.data._
import cats.effect.kernel.Sync

trait Users[F[_]] {
  def create(user: User): F[User]
  def find(id: UserId): F[Option[User]]
}

// TODO [nh] implement me
final class UserService[F[_]: Sync]() extends Users[F] {
  override def create(user: User): F[User] = ???

  override def find(id: UserId): F[Option[User]] = ???

}
