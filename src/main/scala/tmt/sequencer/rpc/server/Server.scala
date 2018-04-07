package tmt.sequencer.rpc.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult._
import akka.stream.{ActorMaterializer, OverflowStrategy}
import covenant.ws.AkkaWsRoute
import covenant.ws.api.WsApiConfigurationWithDefaults
import mycelium.server.WebsocketServerConfig
import tmt.sequencer.rpc.{Advanced, Basic, Streaming}

import scala.concurrent.Future

object Server {
  import sloth._
  import boopickle.Default._
  import chameleon.ext.boopickle._
  import java.nio.ByteBuffer
  import cats.implicits._
  import covenant.http._
  import ByteBufferImplicits._

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem             = ActorSystem("server")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import monix.execution.Scheduler.Implicits.global

    val api = new WsApiConfigurationWithDefaults[Int, String, List[Int]] {
      override def dsl: Dsl.type                               = Dsl
      override def initialState: List[Int]                     = Nil
      override def isStateValid(state: List[Int]): Boolean     = state.length < 10000
      override def serverFailure(error: ServerFailure): String = error.toString
    }
    val config   = WebsocketServerConfig(bufferSize = 5, overflowStrategy = OverflowStrategy.fail)
    val wsRouter = Router[ByteBuffer, Dsl.ApiFunction].route[Streaming[Dsl.ApiFunction]](DslApiImpl)

    val route = {
      pathPrefix("something-later") {
        complete("test-done")
      } ~
      AkkaHttpRoute.fromFutureRouter {
        Router[ByteBuffer, Future]
          .route[Basic](BasicImpl)
          .route[Advanced](AdvancedImpl)
      } ~
      AkkaWsRoute.fromApiRouter(wsRouter, config, api)
    }

    Http().bindAndHandle(route, interface = "0.0.0.0", port = 9090)
  }
}
