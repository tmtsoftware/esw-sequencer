package tmt.sequencer.covenant_copy

import org.scalatest._
import covenant.core.api._
import covenant.ws._
import sloth._
import chameleon.ext.circe._
import io.circe.generic.auto._
import mycelium.client._
import mycelium.server._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult._
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.actor.ActorSystem
import monix.reactive.Observable

import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble
import scala.language.higherKinds

class WsSpec2 extends AsyncFreeSpec with MustMatchers with BeforeAndAfterAll {
  type Event = String
  type State = String
  case class ApiError(msg: String)

  type Dsl = ApiDsl[Event, ApiError, State]

  object Dsl extends Dsl {
    override def applyEventsToState(state: State, events: Seq[Event]): State =
      state + " " + events.mkString(",")
    override def unhandledException(t: Throwable): ApiError =
      ApiError(t.getMessage)
  }

  trait Api[Result[_]] {
    def fun(a: Int): Result[Int]
  }

  object DslApiImpl extends Api[Dsl.ApiFunction] {
    import Dsl._

    def fun(a: Int): ApiFunction[Int] = Action { state =>
      val iterator = Iterator.from(1).map(x => List((a * x).toString))
      val obs      = Observable.fromIterator(iterator).delayOnNext(1.second)
      Future.successful(Returns(a, obs))
    }
  }

  implicit val system: ActorSystem             = ActorSystem("mycelium")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def afterAll(): Unit = {
    system.terminate()
  }

  "run" in {
    import covenant.ws.api._
    import monix.execution.Scheduler.Implicits.global

    val port = 9991

    val api = new WsApiConfigurationWithDefaults[Event, ApiError, State] {
      override def dsl: Dsl                                      = Dsl
      override def initialState: State                           = ""
      override def isStateValid(state: State): Boolean           = true
      override def serverFailure(error: ServerFailure): ApiError = ApiError(error.toString)
    }

    object Backend {
      val router: Router[String, Dsl.ApiFunction] =
        Router[String, Dsl.ApiFunction].route[Api[Dsl.ApiFunction]](DslApiImpl)

      def run(): Future[Http.ServerBinding] = {
        val config = WebsocketServerConfig(bufferSize = 5, overflowStrategy = OverflowStrategy.fail)
        val route  = AkkaWsRoute.fromApiRouter(router, config, api)
        Http().bindAndHandle(route, interface = "0.0.0.0", port = port)
      }
    }

    object Frontend {
      val config                                                             = WebsocketClientConfig()
      val client: WsClient[String, Future, Event, ApiError, ClientException] = WsClient(s"ws://localhost:$port/ws", config)
      val api: Api[Future]                                                   = client.sendWithDefault.wire[Api[Future]]
    }

    Backend.run()

    val doneF = Frontend.client.observable.event.foreach(println)

    Frontend.api.fun(1)
    Frontend.api.fun(10)

    doneF.map(x => 1 mustEqual 1)

  }
}
