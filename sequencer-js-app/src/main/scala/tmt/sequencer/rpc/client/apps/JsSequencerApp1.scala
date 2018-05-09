package tmt.sequencer.rpc.client.apps

import tmt.sequencer.models.{Command, CommandList, Id}
import tmt.sequencer.rpc.client.SequencerClient

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

object JsSequencerApp1 {
  def run(): Unit = {
    val client = new SequencerClient("http://0.0.0.0:9000")
    client.feed(
      CommandList.from(
        Command(Id("1"), "setup-iris", List(1, 2))
      )
    )
  }
}
