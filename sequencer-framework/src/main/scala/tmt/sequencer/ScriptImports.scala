package tmt.sequencer

import scala.concurrent.duration.DurationDouble
import scala.language.implicitConversions

object ScriptImports {

  implicit def toDuration(d: Double): DurationDouble = new DurationDouble(d)

  type Done = akka.Done
  val Done = akka.Done

  type Script         = dsl.Script
  type CswServices    = dsl.CswServices
  type SequencerEvent = models.SequencerEvent
  val SequencerEvent = models.SequencerEvent

  type Command = tmt.sequencer.models.Command
  val Command = tmt.sequencer.models.Command

  type CommandResponse = tmt.sequencer.models.CommandResponse
  val CommandResponse = tmt.sequencer.models.CommandResponse

  type AggregateResponse = tmt.sequencer.models.AggregateResponse
  val AggregateResponse = tmt.sequencer.models.AggregateResponse

  type Future[T] = scala.concurrent.Future[T]

  type Id = tmt.sequencer.models.Id
  val Id = tmt.sequencer.models.Id

}
