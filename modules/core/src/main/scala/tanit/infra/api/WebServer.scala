package tanit.infra.api

import scala.concurrent.duration._

import cats.effect.kernel.Async
import com.comcast.ip4s.{ Host, Port }
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.defaults.Banner
import org.http4s.server.middleware._
import org.http4s.{ HttpApp, HttpRoutes }
import org.typelevel.log4cats

final class WebServer[F[_]: Async: log4cats.Logger](routes: HttpRoutes[F]) {

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60.seconds)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  private val httpApp = loggers(middleware(routes).orNotFound)

  // TODO [NH] make configurable
  val mkServer = EmberServerBuilder
    .default[F]
    .withHost(Host.fromString("0.0.0.0").get)
    .withPort(Port.fromInt(8080).get)
    .withHttpApp(httpApp)
    .build
    .evalTap(_ => log4cats.Logger[F].info(s"\n${Banner.mkString("\n")}\n\nHTTP Server started: http://localhost:8080"))

}
