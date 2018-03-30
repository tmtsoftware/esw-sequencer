package tmt.sequencer.core

import akka.Done
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.{ActorSystem, Scheduler}
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import tmt.sequencer.dsl.Script
import tmt.sequencer.models.SequencerMsg.{GetNext, Update}
import tmt.sequencer.models._

import scala.async.Async._
import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble

class Engine(implicit system: ActorSystem, mat: Materializer) {
  private implicit val timeout: Timeout     = Timeout(5.days)
  private implicit val scheduler: Scheduler = system.scheduler

  import mat.executionContext

  def start(sequencer: Sequencer, script: Script): Future[Done] = {
    Source.repeat(()).mapAsync(1)(_ => execute(sequencer, script)).runForeach(_ => ())
  }

  def execute(sequencer: Sequencer, script: Script): Future[Unit] = async {
    val step = await(sequencer.next)
    step.command.name match {
      case x if x.startsWith("setup-") =>
        val commandResults = await(script.execute(step.command))
        val updatedStep    = step.withResults(commandResults).withStatus(StepStatus.Finished)
        sequencer.update(updatedStep)
      case _ =>
    }
  }
}
