package tmt.sequencer.rpc.apps

import akka.actor.ActorSystem
import tmt.sequencer.models.{Command, CommandList, Id}
import tmt.sequencer.rpc.client.JvmSequencerClient

object JvmSequencerApp {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("test")
    val client                            = new JvmSequencerClient("http://0.0.0.0:9000")
    client.sequenceFeeder.feed(
      CommandList.from(
        Command(Id("A"), "setup-iris", List()),
        Command(Id("B"), "setup-iris", List())
      )
    )
  }
}
