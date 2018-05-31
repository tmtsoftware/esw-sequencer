package tmt.sequencer.rpc.client.apps

import java.nio.ByteBuffer

import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.impl.monix.FetchMonixBackend
import monix.eval.Task
import monix.reactive.Observable
import tmt.sequencer.SequenceFeederClient
import tmt.sequencer.models.{Command, CommandList, Id}
import TaskToFuture.taskToFuture

object JsSequencerApp2 {
  def run(): Unit = {

    import monix.execution.Scheduler.Implicits.global
    implicit val sttpBackend: SttpBackend[Task, Observable[ByteBuffer]] = FetchMonixBackend()

    val client = new SequenceFeederClient[Task, Observable[ByteBuffer]]("http://0.0.0.0:9000")

    client.feed(
      CommandList.from(
        Command(Id("A"), "setup-iris", List()),
        Command(Id("B"), "setup-iris", List())
      )
    )
  }
}
