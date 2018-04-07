package tmt.sequencer.rpc.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import covenant.ws.WsClient
import mycelium.client.WebsocketClientConfig
import tmt.sequencer.rpc.{Advanced, Basic, Streaming}

import scala.concurrent.Future

object Client {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem             = ActorSystem("client")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import monix.execution.Scheduler.Implicits.global

    import sloth._
    import boopickle.Default._
    import chameleon.ext.boopickle._
    import cats.implicits._
    import java.nio.ByteBuffer
    import covenant.http._
    import ByteBufferImplicits._

    val client: Client[ByteBuffer, Future, ClientException] = HttpClient[ByteBuffer]("http://0.0.0.0:9090")
    val basic: Basic                                        = client.wire[Basic]
    val advanced: Advanced                                  = client.wire[Advanced]

    basic.increment(10).foreach { num =>
      println(s"Got response: $num")
    }

    advanced.square(10).foreach { num =>
      println(s"Got response: $num")
    }

    /////////////////////////
    val config                                                               = WebsocketClientConfig()
    val wsClient: WsClient[ByteBuffer, Future, Int, String, ClientException] = WsClient(s"ws://0.0.0.0:9090/ws", config)
    val streaming: Streaming[Future]                                         = wsClient.sendWithDefault.wire[Streaming[Future]]

    wsClient.observable.event.foreach(println)

    streaming.from(78).onComplete { res =>
      println(s"Got response: $res")
    }

  }
}
