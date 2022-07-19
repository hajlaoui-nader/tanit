package tanit.infra.repository

import tanit.domain.Users
import tanit.domain.data
import com.sksamuel.elastic4s.ElasticProperties
import com.sksamuel.elastic4s.ElasticClient

// TODO [NH] implement me
class OpensearchUsers[F[_]] extends Users[F] {

  val props  = ElasticProperties("http://host1:9200")
  // val client = ElasticClient(JavaClient(props))

  override def create(user: data.User): F[data.User] = ???

  override def find(id: data.UserId): F[Option[data.User]] = ???
}
