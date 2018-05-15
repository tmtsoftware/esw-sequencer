package tmt.sequencer.rpc.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult._
import akka.stream.ActorMaterializer
import covenant.http.AkkaHttpRoute
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

import scala.concurrent.Future

class RpcServer(rpcConfigs: RpcConfigs, routes: Routes)(implicit system: ActorSystem) {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  import materializer.executionContext

  implicit val jsonMarshaller: ToResponseMarshaller[String] = Marshaller.fromResponse.compose[String] { str =>
    HttpResponse(
      status = StatusCodes.OK,
      entity = HttpEntity(ContentTypes.`application/json`, str)
    )
  }

  private val route = cors() {
    pathPrefix("something-later") {
      complete("test-done")
    } ~
    AkkaHttpRoute.fromFutureRouter(routes.value)
  }

  def start(): Future[Http.ServerBinding] = Http().bindAndHandle(route, interface = "0.0.0.0", port = rpcConfigs.port)
}
