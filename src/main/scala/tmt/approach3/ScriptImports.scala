package tmt.approach3

import ammonite.ops.Path
import tmt.approach3

import scala.reflect.{classTag, ClassTag}

object ScriptImports {
  @volatile
  private var tag: ClassTag[_] = _

  type Script         = approach3.Script
  type CommandService = tmt.sequencer.CommandService
  type Command        = tmt.services.Command
  type SequencerEvent = tmt.approach3.ScriptRunnerMsg.SequencerEvent

  val Command = tmt.services.Command

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
