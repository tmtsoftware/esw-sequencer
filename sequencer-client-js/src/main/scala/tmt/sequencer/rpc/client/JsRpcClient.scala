package tmt.sequencer.rpc.client

import scala.concurrent.ExecutionContext
import sloth._
import boopickle.Default._
import chameleon.ext.boopickle._
import cats.implicits._
import java.nio.ByteBuffer

import covenant.http._
import tmt.sequencer.api.{SequenceManager, SequenceProcessor}

import scala.concurrent.Future

class JsRpcClient(baseUri: String)(implicit ec: ExecutionContext) {
  private val client: Client[ByteBuffer, Future, ClientException] = HttpClient[ByteBuffer](baseUri)

  val sequenceProcessor: SequenceProcessor = client.wire[SequenceProcessor]
  val sequenceManager: SequenceManager     = client.wire[SequenceManager]
}
