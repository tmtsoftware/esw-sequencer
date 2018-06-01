package tmt.sequencer.rpc.client.apps

import java.nio.ByteBuffer

import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.impl.monix.FetchMonixBackend
import monix.eval.Task
import monix.reactive.Observable
import tmt.sequencer.Streaming2Client

object JsStreamingApp2 {
  def run(): Unit = {
    import monix.execution.Scheduler.Implicits.global

    implicit val value: SttpBackend[Task, Observable[ByteBuffer]] = FetchMonixBackend()

    val client = new Streaming2Client("http://0.0.0.0:9000")
    client.from(23).foreach(println)
  }
}
