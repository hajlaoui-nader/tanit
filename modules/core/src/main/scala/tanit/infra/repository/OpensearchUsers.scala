package tanit.infra.repository

import scala.util.control.NoStackTrace

import tanit.domain.{ Users, data }
import tanit.infra.repository

import cats.effect.kernel.Async
import cats.implicits._
import com.sksamuel.elastic4s.circe._
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.{ ElasticClient, RequestFailure, RequestSuccess }
import io.circe.generic.auto._

import CatsEffectInstancesExecutor._

class OpensearchUsers[F[_]: Async](client: ElasticClient) extends Users[F] {

  case class ElasticError(message: String) extends NoStackTrace

  import com.sksamuel.elastic4s.ElasticDsl._

  override def create(user: data.User): F[data.User] = {
    client
      .execute(
        indexInto("users").source(repository.data.User.fromDomain(user)).refresh(RefreshPolicy.Immediate)
      )
      .flatMap(
        x =>
          x match {
            case RequestFailure(_, _, _, error) =>
              Async[F].raiseError(ElasticError(error.causedBy.map(_.reason).getOrElse("unknown error")))
            case RequestSuccess(_, _, _, _) => Async[F].pure(user)
          }
      )

  }

  // TODO [NH] implement me
  override def find(id: data.UserId): F[Option[data.User]] = ???
}
