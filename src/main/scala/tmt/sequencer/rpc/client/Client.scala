package tmt.sequencer.rpc.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.sequencer.rpc.{Advanced, Basic}

import scala.concurrent.Future

object Client {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem             = ActorSystem("client")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import materializer.executionContext

    import sloth._
    import boopickle.Default._
    import chameleon.ext.boopickle._
    import cats.implicits._
    import java.nio.ByteBuffer
    import covenant.http._
    import ByteBufferImplicits._

    val baseUri = "http://0.0.0.0:9090"

    val client: Client[ByteBuffer, Future, ClientException] = HttpClient[ByteBuffer](baseUri)

    val basic: Basic       = client.wire[Basic]
    val advanced: Advanced = client.wire[Advanced]

    basic.increment(10).foreach { num =>
      println(s"Got response: $num")
    }

    advanced.square(10).foreach { num =>
      println(s"Got response: $num")
    }
  }
}
