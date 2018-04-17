package tmt.sequencer.rpc.apps

import akka.actor.ActorSystem
import monix.reactive.Observable
import tmt.sequencer.rpc.client.JvmStreamingClient

object JvmStreamingApp {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("test")
    import monix.execution.Scheduler.Implicits.global

    val client = new JvmStreamingClient("ws://0.0.0.0:9090/ws")

    client.events.flatMap(xs => Observable.fromIterable(xs)).foreach(println)

    client.streaming.from(78).onComplete { res =>
      println(s"Got response: $res")
    }
  }
}
