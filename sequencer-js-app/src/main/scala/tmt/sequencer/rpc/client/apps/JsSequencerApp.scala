package tmt.sequencer.rpc.client.apps

import tmt.sequencer.models.{Command, Id}
import tmt.sequencer.rpc.client.JsSequencerClient

object JsSequencerApp {
  def run(): Unit = {
    val client = new JsSequencerClient("http://0.0.0.0:9000")
    client.sequenceFeeder.feed(
      List(
        Command(Id("A"), "setup-iris", List()),
        Command(Id("B"), "setup-iris", List())
      )
    )
  }
}
