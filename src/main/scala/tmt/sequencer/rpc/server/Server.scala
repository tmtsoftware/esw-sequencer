package tmt.sequencer.rpc.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult._
import akka.stream.ActorMaterializer
import covenant.ws.AkkaWsRoute
import tmt.sequencer.rpc.{Advanced, Basic}

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
    import materializer.executionContext

    val route = {
      pathPrefix("something-later") {
        complete("test-done")
      } ~
      AkkaHttpRoute.fromFutureRouter {
        Router[ByteBuffer, Future]
          .route[Basic](BasicImpl)
          .route[Advanced](AdvancedImpl)
      }
    }

    Http().bindAndHandle(route, interface = "0.0.0.0", port = 9090)
  }
}
