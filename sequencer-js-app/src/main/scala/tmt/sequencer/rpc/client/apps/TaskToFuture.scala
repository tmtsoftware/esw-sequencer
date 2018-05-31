package tmt.sequencer.rpc.client.apps

import monix.eval.Task
import monix.execution.Scheduler
import tmt.sequencer.ToFuture

import scala.concurrent.Future

object TaskToFuture {
  implicit def taskToFuture(implicit s: Scheduler): ToFuture[Task] = new ToFuture[Task] {
    override def convert[A](x: Task[A]): Future[A] = x.runAsync
  }
}
