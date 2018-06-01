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
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.AddAll}")
      .body(commands)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def pause(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Pause}")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def resume(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Resume}")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def reset(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Reset}")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def sequence: Future[Sequence] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Sequence}")
      .response(asJson[Sequence])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def delete(ids: List[models.Id]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Delete}")
      .body(ids)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def addBreakpoints(ids: List[models.Id]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.AddBreakpoints}")
      .body(ids)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def removeBreakpoints(ids: List[models.Id]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.RemoveBreakpoints}")
      .body(ids)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def insertAfter(id: models.Id, commands: List[Command]): Future[Unit] = {
    val payLoad = (id, commands)
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.InsertAfter}")
      .body(payLoad)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def prepend(commands: List[Command]): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Prepend}")
      .body(commands)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def replace(id: models.Id, commands: List[Command]): Future[Unit] = {
    val payLoad = (id, commands)
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Replace}")
      .body(payLoad)
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }

  override def shutdown(): Future[Unit] = {
    sttp
      .post(uri"$baseUri/${SequenceEditor.ApiName}/${SequenceEditor.Shutdown}")
      .response(asJson[Unit])
      .send()
      .asFuture
      .map(_.unsafeBody.right.get)
  }
}
