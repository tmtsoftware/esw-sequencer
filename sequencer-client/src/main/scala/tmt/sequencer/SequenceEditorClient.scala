package tmt.sequencer

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.generic.auto._
import tmt.sequencer.ToFuture.RichR
import tmt.sequencer.api.SequenceEditor
import tmt.sequencer.models.{Command, Sequence}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class SequenceEditorClient[R[_]: ToFuture, -S](baseUri: String)(implicit backend: SttpBackend[R, S], ec: ExecutionContext)
    extends SequenceEditor {

  override def addAll(commands: List[Command]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/addAll")
      .body(commands)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def pause(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/pause")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def resume(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/resume")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def reset(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/reset")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def sequence: Future[Sequence] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/sequence")
      .response(asJson[Sequence])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def delete(ids: List[models.Id]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/delete")
      .body(ids)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def addBreakpoints(ids: List[models.Id]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/addBreakpoints")
      .body(ids)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def removeBreakpoints(ids: List[models.Id]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/removeBreakpoints")
      .body(ids)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def insertAfter(id: models.Id, commands: List[Command]): Future[Unit] = {
    val payLoad = (id, commands)
    sttp
      .post(uri"$baseUri/SequenceEditor/insertAfter")
      .body(payLoad)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def prepend(commands: List[Command]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/prepend")
      .body(commands)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def replace(id: models.Id, commands: List[Command]): Future[Unit] = {
    val payLoad = (id, commands)
    sttp
      .post(uri"$baseUri/SequenceEditor/replace")
      .body(payLoad)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def shutdown(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/SequenceEditor/shutdown")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }
}
