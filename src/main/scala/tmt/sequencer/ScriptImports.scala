package tmt.sequencer

import ammonite.ops.Path
import tmt.sequencer.dsl.ScriptFactory
import tmt.sequencer.models.EngineMsg

import scala.concurrent.duration.DurationDouble
import scala.language.implicitConversions
import scala.reflect.{classTag, ClassTag}

object ScriptImports {

  implicit def toDuration(d: Double): DurationDouble = new DurationDouble(d)

  @volatile
  private var tag: ClassTag[_] = _

  type Script         = dsl.Script
  type CswServices    = gateway.CswServices
  type SequencerEvent = EngineMsg.SequencerEvent
  val SequencerEvent = EngineMsg.SequencerEvent

  type Command = tmt.sequencer.models.Command
  val Command = tmt.sequencer.models.Command

  type CommandResult = tmt.sequencer.models.CommandResult
  val CommandResult = tmt.sequencer.models.CommandResult

  type CommandResults = tmt.sequencer.models.CommandResults
  val CommandResults = tmt.sequencer.models.CommandResults

  type Future[T] = scala.concurrent.Future[T]

  type Id = tmt.sequencer.models.Id
  val Id = tmt.sequencer.models.Id

  private[tmt] def load(path: Path): ScriptFactory = synchronized {
    ammonite.Main().runScript(path, Seq.empty) match {
      case (x, _) => println(s"script loading status: $x")
    }
    val constructor = tag.runtimeClass.getConstructors.toList.head
    constructor.newInstance().asInstanceOf[ScriptFactory]
  }

  def init[T <: ScriptFactory: ClassTag]: Unit = {
    tag = classTag[T]
  }
}
