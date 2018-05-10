package tmt.sequencer.rpc.client

import io.circe.generic.auto._
import chameleon.ext.circe._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import covenant.ws.WsClient
import monix.reactive.Observable
import mycelium.client.WebsocketClientConfig
import tmt.sequencer.api.Streaming

import scala.concurrent.Future

class JvmStreamingClient(baseUri: String)(implicit system: ActorSystem) {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  import materializer.executionContext

  private val config               = WebsocketClientConfig()
  private val wsClient             = WsClient[String, Int, String](s"ws://0.0.0.0:9090/ws", config)
  val streaming: Streaming[Future] = wsClient.sendWithDefault.wire[Streaming[Future]]

  val events: Observable[List[Int]] = wsClient.observable.event
}
