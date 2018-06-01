package tmt.sequencer

import java.nio.ByteBuffer

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.generic.auto._
import monix.eval.Task
import monix.reactive.Observable
import tmt.sequencer.ToFuture.RichR
import tmt.sequencer.api.Streaming2

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class Streaming2Client(baseUri: String)(implicit backend: SttpBackend[Task, Observable[ByteBuffer]], ec: ExecutionContext)
    extends Streaming2[Observable] {

  import monix.execution.Scheduler.Implicits.global

  override def from(a: Int): Observable[String] = {
    Observable
      .fromFuture(
        sttp
          .get(uri"$baseUri/${Streaming2.ApiName}/${Streaming2.From}")
          .response(asStream[Observable[ByteBuffer]])
          .send()
          .asFuture
          .map(_.unsafeBody.map { k =>
            k.asCharBuffer().toString
          })
      )
      .flatMap(identity)
  }
}
