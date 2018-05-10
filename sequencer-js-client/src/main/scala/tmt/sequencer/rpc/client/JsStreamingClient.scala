package tmt.sequencer.rpc.client

import covenant.ws.WsClient
import monix.reactive.Observable
import mycelium.client.WebsocketClientConfig
import tmt.sequencer.api.Streaming

import scala.concurrent.Future

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import chameleon.ext.circe._
import java.nio.ByteBuffer

class JsStreamingClient(baseUri: String) {
  import scala.concurrent.ExecutionContext.Implicits.global

  private val config               = WebsocketClientConfig()
  private val wsClient             = WsClient[String, Int, String](baseUri, config)
  val streaming: Streaming[Future] = wsClient.sendWithDefault.wire[Streaming[Future]]

  val events: Observable[List[Int]] = wsClient.observable.event
}
