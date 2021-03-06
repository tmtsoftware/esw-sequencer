package tmt.sequencer.covenant_copy

import org.scalatest._
import covenant.core.api._
import covenant.http._
import sloth._
import chameleon.ext.circe._
import io.circe.generic.auto._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult._
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, Materializer}
import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.FromByteStringUnmarshaller
import akka.util.ByteString
import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class HttpSpec extends AsyncFreeSpec with MustMatchers with BeforeAndAfterAll {

  implicit val StringUnmarshaller: FromByteStringUnmarshaller[String] = new FromByteStringUnmarshaller[String] {
    def apply(value: ByteString)(implicit ec: ExecutionContext, materializer: Materializer): Future[String] =
      Future.successful(value.utf8String)
  }

  trait Api[Result[_]] {
    def fun(a: Int): Result[Int]
    @PathName("funWithDefault")
    def fun(a: Int, b: Int): Result[Int] = fun(a + b)
  }

  object FutureApiImpl extends Api[Future] {
    def fun(a: Int): Future[Int] = Future.successful(a)
  }

  object DslApiImpl extends Api[Dsl.ApiFunction] {
    import Dsl._

    def fun(a: Int): ApiFunction[Int] = Action { state =>
      Future.successful(a)
    }
  }

  //TODO generalize over this structure, can implement requesthander? --> apidsl
  type Event = String
  type State = String

  case class ApiValue[T](result: T, events: List[Event])
  case class ApiResult[T](state: Future[State], value: Future[ApiValue[T]])
  type ApiResultFun[T] = Future[State] => ApiResult[T]

  case class ApiError(msg: String)

  implicit val apiValueFunctor     = cats.derive.functor[ApiValue]
  implicit val apiResultFunctor    = cats.derive.functor[ApiResult]
  implicit val apiResultFunFunctor = cats.derive.functor[ApiResultFun]

  object Dsl extends ApiDsl[Event, ApiError, State] {
    override def applyEventsToState(state: State, events: Seq[Event]): State =
      state + " " + events.mkString(",")
    override def unhandledException(t: Throwable): ApiError =
      ApiError(t.getMessage)
  }
  //

  implicit val system       = ActorSystem("akkahttp")
  implicit val materializer = ActorMaterializer()

  "simple run" in {
    val port = 9989

    object Backend {
      val router = Router[String, Future]
        .route[Api[Future]](FutureApiImpl)

      def run() = {
        Http().bindAndHandle(AkkaHttpRoute.fromFutureRouter(router), interface = "0.0.0.0", port = port)
      }
    }

    object Frontend {
      val client = HttpClient[String](s"http://localhost:$port")
      val api    = client.wire[Api[Future]]
    }

    Backend.run()

    for {
      fun  <- Frontend.api.fun(1)
      fun2 <- Frontend.api.fun(1, 2)
    } yield {
      fun mustEqual 1
      fun2 mustEqual 3
    }
  }

  "run" in {
    import covenant.http.api._
    import monix.execution.Scheduler.Implicits.global

    val port = 9988

    val api = new HttpApiConfiguration[Event, ApiError, State] {
      override def requestToState(request: HttpRequest): Future[State] =
        Future.successful(request.toString)
      override def publishEvents(events: List[Event]): Unit = ()
    }

    object Backend {
      val router = Router[String, Dsl.ApiFunction]
        .route[Api[Dsl.ApiFunction]](DslApiImpl)

      def run() = {
        val route = AkkaHttpRoute.fromApiRouter(router, api)
        Http().bindAndHandle(route, interface = "0.0.0.0", port = port)
      }
    }

    object Frontend {
      val client = HttpClient[String](s"http://localhost:$port")
      val api    = client.wire[Api[Future]]
    }

    Backend.run()

    for {
      fun  <- Frontend.api.fun(1)
      fun2 <- Frontend.api.fun(1, 2)
    } yield {
      fun mustEqual 1
      fun2 mustEqual 3
    }
  }

  override def afterAll(): Unit = {
    system.terminate()
    ()
  }
}
