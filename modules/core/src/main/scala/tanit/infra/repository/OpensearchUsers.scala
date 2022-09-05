package tanit.infra.repository

import tanit.domain.Users
import tanit.domain.data
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.Executor
import com.sksamuel.elastic4s.Functor
import cats.implicits._
// TODO [NH] implement me
class OpensearchUsers[F[_]](client: ElasticClient) extends Users[F] {
  import com.sksamuel.elastic4s.ElasticDsl._

  override def create(user: data.User): F[data.User] = {
   
    val y = client.execute(
      indexInto("artists").fields("name" -> "L.S. Lowry").refresh(RefreshPolicy.Immediate)
    )
    ???
  }

  override def find(id: data.UserId): F[Option[data.User]] = ???
}
