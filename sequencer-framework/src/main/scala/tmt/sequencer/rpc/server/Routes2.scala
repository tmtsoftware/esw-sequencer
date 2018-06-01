package tmt.sequencer.rpc.server

import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder, Streaming2}
import tmt.sequencer.models
import tmt.sequencer.models.{Command, CommandList}
import tmt.sequencer.rpc.server.spike.A
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

class Routes2(sequenceFeeder: SequenceFeeder, sequenceEditor: SequenceEditor, streaming2: Streaming2[A.S])(
    implicit mat: Materializer
) extends FailFastCirceSupport {

  val route: Route = cors() {
    post {
      pathPrefix(SequenceFeeder.ApiName) {
        path(SequenceFeeder.Feed) {
          entity(as[CommandList]) { commandList =>
            complete(sequenceFeeder.feed(commandList))
          }
        }
      } ~
      pathPrefix(SequenceEditor.ApiName) {
        path(SequenceEditor.AddAll) {
          entity(as[List[Command]]) { commands =>
            complete(sequenceEditor.addAll(commands))
          }
        } ~
        path(SequenceEditor.Pause) {
          entity(as[Unit]) { commands =>
            complete(sequenceEditor.pause())
          }
        } ~
        path(SequenceEditor.Resume) {
          entity(as[Unit]) { commands =>
            complete(sequenceEditor.resume())
          }
        } ~
        path(SequenceEditor.Reset) {
          entity(as[Unit]) { commands =>
            complete(sequenceEditor.reset())
          }
        } ~
        path(SequenceEditor.Delete) {
          entity(as[List[models.Id]]) { ids =>
            complete(sequenceEditor.delete(ids))
          }
        } ~
        path(SequenceEditor.AddBreakpoints) {
          entity(as[List[models.Id]]) { ids =>
            complete(sequenceEditor.addBreakpoints(ids))
          }
        } ~
        path(SequenceEditor.RemoveBreakpoints) {
          entity(as[List[models.Id]]) { ids =>
            complete(sequenceEditor.removeBreakpoints(ids))
          }
        } ~
        path(SequenceEditor.Prepend) {
          entity(as[List[Command]]) { commands =>
            complete(sequenceEditor.prepend(commands))
          }
        } ~
        path(SequenceEditor.Replace) {
          entity(as[(models.Id, List[Command])]) {
            case (id, commands) =>
              complete(sequenceEditor.replace(id, commands))
          }
        } ~
        path(SequenceEditor.Shutdown) {
          entity(as[Unit]) { commands =>
            complete(sequenceEditor.shutdown())
          }
        }
      }
    } ~
    get {
      pathPrefix(Streaming2.ApiName) {
        path(Streaming2.From) {
          handleWebSocketMessages(
            Flow[Message].mapConcat {
              case _ => TextMessage(streaming2.from(23)) :: Nil
//              case TextMessage.Streamed(textStream) => textStream.runWith(Sink.ignore); Nil
//              case bm: BinaryMessage                => bm.dataStream.runWith(Sink.ignore); Nil
            }
          )
        }
      }
    }
  }
}
