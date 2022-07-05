package tanit.infra.repository

import tanit.domain.Users
import tanit.domain.data

// TODO [NH] implement me
class OpensearchUsers[F[_]] extends Users[F] {

  // val client = ElasticClient()

  override def create(user: data.User): F[data.User] = ???

  override def find(id: data.UserId): F[Option[data.User]] = ???
}
