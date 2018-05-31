package tmt.sequencer.rpc.apps

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import tmt.sequencer.SequenceFeederClient
import tmt.sequencer.models.{Command, CommandList, Id}

import scala.concurrent.Future

object JvmSequencerApp2 {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val sttpBackend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()

    val client = new SequenceFeederClient[Future, Source[ByteString, Any]]("http://0.0.0.0:9000")

    client.feed(
      CommandList.from(
        Command(Id("A"), "setup-iris", List()),
        Command(Id("B"), "setup-iris", List())
      )
    )
  }
}
