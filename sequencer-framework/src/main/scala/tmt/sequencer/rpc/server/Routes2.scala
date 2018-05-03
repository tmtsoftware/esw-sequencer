package tmt.sequencer.rpc.server

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import scalapb.json4s.JsonFormat
import sequencer_protobuf.command.PbMyJson
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.models.Msg

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
      } ~
      path("sayhello2") {
        //hardcoded json string
        val proto: PbMyJson = JsonFormat.fromJsonString[PbMyJson]("""{"jsonValue": "poorva"}""")
        println("**********" + proto.toString)
        val response: Future[PbMyJson] = sequenceFeeder.sayHello2(proto)
        onComplete(response) { done =>
//          val jsonResponse = JsonFormat.toJsonString(response)
          complete("called sayHello22222")
        }

      }
    }
}
