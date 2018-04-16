package tmt.sequencer.rpc.client

import boopickle.Default._
import chameleon.ext.boopickle._
import java.nio.ByteBuffer

import covenant.http.HttpClient
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}

class JsSequencerClient(baseUri: String) {
  import monix.execution.Scheduler.Implicits.global

  private val client = HttpClient[ByteBuffer](baseUri)

  val sequenceFeeder: SequenceFeeder = client.wire[SequenceFeeder]
  val sequenceEditor: SequenceEditor = client.wire[SequenceEditor]
}
