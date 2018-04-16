package tmt.sequencer.rpc.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import covenant.http.HttpClient
import sloth.{Client, ClientException}
import boopickle.Default._
import chameleon.ext.boopickle._
import java.nio.ByteBuffer

import covenant.http._
import ByteBufferImplicits._
import tmt.sequencer.api.{SequenceManager, SequenceProcessor}

import scala.concurrent.Future

class JvmSequencerClient(baseUri: String)(implicit system: ActorSystem) {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val client: Client[ByteBuffer, Future, ClientException] = HttpClient[ByteBuffer](baseUri)

  val sequenceProcessor: SequenceProcessor = client.wire[SequenceProcessor]
  val sequenceManager: SequenceManager     = client.wire[SequenceManager]
}
