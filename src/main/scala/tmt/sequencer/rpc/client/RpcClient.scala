package tmt.sequencer.rpc.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import covenant.http.HttpClient
import sloth.{Client, ClientException}
import tmt.sequencer.rpc.api.SequenceProcessor
import boopickle.Default._
import chameleon.ext.boopickle._
import cats.implicits._
import java.nio.ByteBuffer
import covenant.http._
import ByteBufferImplicits._

import scala.concurrent.Future

class RpcClient(implicit system: ActorSystem) {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  val client: Client[ByteBuffer, Future, ClientException] = HttpClient[ByteBuffer]("http://0.0.0.0:9090")
  val sequenceProcessor: SequenceProcessor                = client.wire[SequenceProcessor]
}
