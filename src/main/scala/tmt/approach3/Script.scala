package tmt.approach3

import ammonite.ops.Path
import tmt.approach3.sequencer.{ControlDsl, HelperDsl}
import tmt.sequencer.{CommandService, Engine}
import tmt.services.Command

import scala.reflect.{ClassTag, classTag}

abstract class Script(cs: CommandService, engine: Engine) extends HelperDsl {
  def onCommand(x: Command): Unit
  def onShutdown(): Unit
}

object Script {
  @volatile
  var tag: ClassTag[_] = _

  type Script = tmt.approach3.Script
  type CommandService = tmt.sequencer.CommandService
  type Engine = tmt.sequencer.Engine
  type Command = tmt.services.Command
  val Command = tmt.services.Command

  def load(path: Path, cs: CommandService, engine: Engine): Script = synchronized {
    ammonite.Main().runScript(path, Seq.empty)
    val constructor = tag.runtimeClass.getConstructors.toList.head
    constructor.newInstance(cs, engine).asInstanceOf[Script]
  }

  def init[T <: Script : ClassTag]: Unit = {
    tag = classTag[T]
  }
}
