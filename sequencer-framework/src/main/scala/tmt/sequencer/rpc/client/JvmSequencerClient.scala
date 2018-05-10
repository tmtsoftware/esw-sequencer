package tmt.sequencer.rpc.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import covenant.http.HttpClient
import sloth.{Client, ClientException}
import upickle.default._
import chameleon.ext.upickle._

import covenant.http._
import ByteBufferImplicits._
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}

import scala.concurrent.Future

class JvmSequencerClient(baseUri: String)(implicit system: ActorSystem) {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val client: Client[String, Future, ClientException] = HttpClient[String](baseUri)

  val sequenceFeeder: SequenceFeeder = client.wire[SequenceFeeder]
  val sequenceEditor: SequenceEditor = client.wire[SequenceEditor]
}
