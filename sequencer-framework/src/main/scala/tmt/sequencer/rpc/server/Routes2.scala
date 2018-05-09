package tmt.sequencer.rpc.server

import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import de.heikoseeberger.akkahttpupickle.UpickleSupport._
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.models.CommandList

import scala.concurrent.ExecutionContext

class Routes2(sequenceFeeder: SequenceFeeder)(implicit ec: ExecutionContext) {
  val route = cors() {
    post {
      path("feed") {
        entity(as[CommandList]) { commandList =>
          complete(sequenceFeeder.feed(commandList))
        }
      }
    }
  }
}
