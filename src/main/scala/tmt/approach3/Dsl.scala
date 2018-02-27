package tmt.approach3

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.CommandResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

trait Dsl {

  implicit def toFuture(x: => CommandResponse): Future[CommandResponse] = Future(x)

  def par(fs: Future[CommandResponse]*): Seq[CommandResponse] = Future.sequence(fs.toList).await

  implicit class RichCommandResponse(commandResponse: => CommandResponse) {
    def async: Future[CommandResponse] = Future(commandResponse)
  }
}
