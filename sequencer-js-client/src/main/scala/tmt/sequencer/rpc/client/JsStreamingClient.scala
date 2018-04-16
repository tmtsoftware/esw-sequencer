package tmt.sequencer.rpc.client

import covenant.ws.WsClient
import monix.reactive.Observable
import mycelium.client.WebsocketClientConfig
import tmt.sequencer.api.Streaming

import scala.concurrent.Future

import boopickle.Default._
import chameleon.ext.boopickle._
import java.nio.ByteBuffer

class JsStreamingClient(baseUri: String) {
  import monix.execution.Scheduler.Implicits.global

  private val config               = WebsocketClientConfig()
  private val wsClient             = WsClient[ByteBuffer, Int, String](baseUri, config)
  val streaming: Streaming[Future] = wsClient.sendWithDefault.wire[Streaming[Future]]

  val events: Observable[List[Int]] = wsClient.observable.event
}
