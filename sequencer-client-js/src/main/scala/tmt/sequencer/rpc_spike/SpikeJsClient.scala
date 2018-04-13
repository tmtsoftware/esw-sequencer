package tmt.sequencer.rpc_spike

import covenant.ws.WsClient
import monix.reactive.Observable
import mycelium.client.WebsocketClientConfig

import scala.concurrent.Future
import scala.util.{Failure, Success}

object SpikeJsClient {
  def main2(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global

    import sloth._
    import boopickle.Default._
    import chameleon.ext.boopickle._
    import java.nio.ByteBuffer
    import covenant.http._

    val client: Client[ByteBuffer, Future, ClientException] = HttpClient[ByteBuffer]("http://localhost:9090/api")
    val basic: Basic                                        = client.wire[Basic]
    val advanced: Advanced                                  = client.wire[Advanced]

    basic.increment(10).onComplete {
      case Success(num) => println(s"Got response: $num")
      case Failure(ex)  => ex.printStackTrace()
    }

    advanced.square(10).onComplete {
      case Success(num) => println(s"Got response2: $num")
      case Failure(ex)  => ex.printStackTrace()
    }

    /////////////////////////
    val config                                                               = WebsocketClientConfig()
    val wsClient: WsClient[ByteBuffer, Future, Int, String, ClientException] = WsClient(s"ws://0.0.0.0:9090/ws", config)
    val streaming: Streaming[Future]                                         = wsClient.sendWithDefault.wire[Streaming[Future]]

    wsClient.observable.event.flatMap(xs => Observable.fromIterable(xs)).foreach(println)

    streaming.from(78).onComplete { res =>
      println(s"Got response: $res")
    }

  }
}
