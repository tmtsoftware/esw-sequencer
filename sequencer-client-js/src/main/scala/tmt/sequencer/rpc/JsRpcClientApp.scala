package tmt.sequencer.rpc

import tmt.sequencer.models.{Command, Id}
import tmt.sequencer.rpc.client.JsRpcClient

object JsRpcClientApp {
  def main2(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global

    val client = new JsRpcClient("http://0.0.0.0:9000")
    client.sequenceProcessor.submitSequence(
      List(
        Command(Id("A"), "setup-iris", List()),
        Command(Id("B"), "setup-iris", List())
      )
    )
  }
}
