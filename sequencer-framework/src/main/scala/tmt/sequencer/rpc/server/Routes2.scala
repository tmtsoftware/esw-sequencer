package tmt.sequencer.rpc.server

import akka.http.scaladsl.server.Directives._
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.models.Msg
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

import scala.concurrent.{ExecutionContext, Future}

class Routes2(sequenceFeeder: SequenceFeeder)(implicit ec: ExecutionContext) {
  val route =
    post {
      path("sayhello") {
        entity(as[Msg]) { msg =>
          val response: Future[Msg] = sequenceFeeder.sayHello(msg)
          onComplete(response) { done =>
            complete(response)
          }
        }
      }
    }
}
