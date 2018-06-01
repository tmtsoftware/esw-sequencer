package tmt.sequencer

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe
import io.circe.generic.auto._
import tmt.sequencer.ToFuture.RichR
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.models.{AggregateResponse, CommandList}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class SequenceFeederClient[R[_]: ToFuture, -S](baseUri: String)(implicit backend: SttpBackend[R, S], ec: ExecutionContext)
    extends SequenceFeeder {

  override def feed(commandList: CommandList): Future[AggregateResponse] = {
    sttp
      .post(uri"$baseUri/${SequenceFeeder.ApiName}/${SequenceFeeder.Feed}")
      .body(commandList)
      .response(asJson[AggregateResponse])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }
}
