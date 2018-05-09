//package tmt.sequencer.rpc.client.apps
//
//import monix.reactive.Observable
//import tmt.sequencer.rpc.client.JsStreamingClient
//
//object JsStreamingApp {
//  def run(): Unit = {
//    import monix.execution.Scheduler.Implicits.global
//
//    val client = new JsStreamingClient("ws://0.0.0.0:9090/ws")
//
//    client.events.flatMap(xs => Observable.fromIterable(xs)).foreach(println)
//
//    client.streaming.from(78).onComplete { res =>
//      println(s"Got response: $res")
//    }
//  }
//}
