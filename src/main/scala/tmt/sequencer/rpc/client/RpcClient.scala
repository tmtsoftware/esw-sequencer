package tmt.sequencer.rpc.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import covenant.http.HttpClient
import sloth.{Client, ClientException}
import tmt.sequencer.rpc.api.{SequenceManager, SequenceProcessor}
import boopickle.Default._
import chameleon.ext.boopickle._
import cats.implicits._
import java.nio.ByteBuffer

import covenant.http._
import ByteBufferImplicits._

import scala.concurrent.Future

class RpcClient(implicit system: ActorSystem) {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  def client(port: Int): Client[ByteBuffer, Future, ClientException] = HttpClient[ByteBuffer](s"http://0.0.0.0:$port")
  def sequenceProcessor(port: Int): SequenceProcessor                = client(port).wire[SequenceProcessor]
  def sequenceManager(port: Int): SequenceManager                    = client(port).wire[SequenceManager]
}
