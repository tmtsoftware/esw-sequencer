package tmt.sequencer

import ammonite.ops.Path
import tmt.sequencer

import scala.reflect.{classTag, ClassTag}

object ScriptImports {
  @volatile
  private var tag: ClassTag[_] = _

  type Script         = tmt.sequencer.Script
  type CommandService = tmt.sequencer.CswServices
  type Command        = tmt.sequencer.Command
  type SequencerEvent = ScriptRunnerMsg.SequencerEvent

  val Command = tmt.sequencer.Command

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
}
