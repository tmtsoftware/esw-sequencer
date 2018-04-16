package tmt.sequencer.rpc.client

import boopickle.Default._
import chameleon.ext.boopickle._
import java.nio.ByteBuffer

import covenant.http.HttpClient
import tmt.sequencer.api.{SequenceManager, SequenceProcessor}

class JsSequencerClient(baseUri: String) {
  import monix.execution.Scheduler.Implicits.global

  private val client = HttpClient[ByteBuffer](baseUri)

  val sequenceProcessor: SequenceProcessor = client.wire[SequenceProcessor]
  val sequenceManager: SequenceManager     = client.wire[SequenceManager]
}
