package tmt.sequencer.rpc.server

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpupickle.UpickleSupport._
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.models.{CommandList, Msg}

import scala.concurrent.ExecutionContext

class Routes2(sequenceFeeder: SequenceFeeder)(implicit ec: ExecutionContext) {
  val route =
    post {
      path("json-route") {
        entity(as[Msg]) { msg =>
          complete(sequenceFeeder.testJsonApi(msg))
        }
      } ~
      path("feed") {
        entity(as[CommandList]) { commandList =>
          complete(sequenceFeeder.feed(commandList))
        }
      }
    }
}
