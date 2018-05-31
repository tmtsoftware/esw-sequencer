package tmt.sequencer.rpc.server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}
import tmt.sequencer.models
import tmt.sequencer.models.{Command, CommandList}

class Routes2(sequenceFeeder: SequenceFeeder, sequenceEditor: SequenceEditor) extends FailFastCirceSupport {
  val route: Route =
    post {
      path("SequenceFeeder" / "feed") {
        entity(as[CommandList]) { commandList =>
          complete(sequenceFeeder.feed(commandList))
        }
      } ~
      path("SequenceEditor" / "addAll") {
        entity(as[List[Command]]) { commands =>
          complete(sequenceEditor.addAll(commands))
        }
      } ~
      path("SequenceEditor" / "pause") {
        entity(as[Unit]) { commands =>
          complete(sequenceEditor.pause())
        }
      } ~
      path("SequenceEditor" / "resume") {
        entity(as[Unit]) { commands =>
          complete(sequenceEditor.resume())
        }
      } ~
      path("SequenceEditor" / "reset") {
        entity(as[Unit]) { commands =>
          complete(sequenceEditor.reset())
        }
      } ~
      path("SequenceEditor" / "delete") {
        entity(as[List[models.Id]]) { ids =>
          complete(sequenceEditor.delete(ids))
        }
      } ~
      path("SequenceEditor" / "addBreakpoints") {
        entity(as[List[models.Id]]) { ids =>
          complete(sequenceEditor.addBreakpoints(ids))
        }
      } ~
      path("SequenceEditor" / "removeBreakpoints") {
        entity(as[List[models.Id]]) { ids =>
          complete(sequenceEditor.removeBreakpoints(ids))
        }
      } ~
      path("SequenceEditor" / "prepend") {
        entity(as[List[Command]]) { commands =>
          complete(sequenceEditor.prepend(commands))
        }
      } ~
      path("SequenceEditor" / "replace") {
        entity(as[(models.Id, List[Command])]) {
          case (id, commands) =>
            complete(sequenceEditor.replace(id, commands))
        }
      } ~
      path("SequenceEditor" / "shutdown") {
        entity(as[Unit]) { commands =>
          complete(sequenceEditor.shutdown())
        }
      }
    }
}
