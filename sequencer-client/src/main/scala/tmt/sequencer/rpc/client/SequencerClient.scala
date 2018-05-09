package tmt.sequencer.rpc.client

import java.nio.ByteBuffer

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method.POST
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.models.{AggregateResponse, CommandList}
import fr.hmil.roshttp.body.{BodyPart, ByteBufferBody}
import fr.hmil.roshttp.response.SimpleHttpResponse

import scala.concurrent.Future
import scala.util.{Failure, Success}
import upickle.default._

class SequencerClient(baseUri: String) extends SequenceFeeder {

  import monix.execution.Scheduler.Implicits.global

  private def feedUri: String = s"$baseUri/feed"

  override def feed(commandList: CommandList): Future[AggregateResponse] = {

    val request = HttpRequest(feedUri).withMethod(POST)

    println("Sending HTTP request")
    val eventualResponse: Future[SimpleHttpResponse] = request
      .post(MyJSONBody(write(commandList)))

    eventualResponse
      .onComplete({
        case response: Success[SimpleHttpResponse] => println("sucessful" + response.value.body)
        case _: Failure[SimpleHttpResponse]        => println("Error")
      })

    Future { AggregateResponse }
  }
}

object MyJSONBody {
  def apply(value: String): BodyPart =
    ByteBufferBody(ByteBuffer.wrap(value.getBytes("utf-8")), "application/json; charset=utf-8");
}
