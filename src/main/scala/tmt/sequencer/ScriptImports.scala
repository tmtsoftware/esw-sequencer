package tmt.sequencer

import ammonite.ops.Path
import reactify.State
import tmt.sequencer.models.EngineMsg

import scala.reflect.{classTag, ClassTag}

object ScriptImports {
  @volatile
  private var tag: ClassTag[_] = _

  type Script         = tmt.sequencer.Script
  type CommandService = tmt.sequencer.CswServices
  type SequencerEvent = EngineMsg.SequencerEvent

  type Command = tmt.sequencer.models.Command
  val Command = tmt.sequencer.models.Command

  type CommandResult = tmt.sequencer.models.CommandResult
  val CommandResult = tmt.sequencer.models.CommandResult

  type Id = tmt.sequencer.models.Id
  val Id = tmt.sequencer.models.Id

  private[tmt] def load(path: Path, cs: CommandService): Script = synchronized {
    ammonite.Main().runScript(path, Seq.empty) match {
      case (x, _) => println(s"script loading status: $x")
    }
    val constructor = tag.runtimeClass.getConstructors.toList.head
    constructor.newInstance(cs).asInstanceOf[Script]
  }

  def init[T <: Script: ClassTag]: Unit = {
    tag = classTag[T]
  }

  implicit def state2Value[T](p: State[T]): T = p()
}
