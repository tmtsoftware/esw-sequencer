package tmt.approach3

import ammonite.ops.Path
import tmt.sequencer.{CommandService, ControlDsl, Engine}
import tmt.services.{Command, CommandResponse}

import scala.concurrent.Future
import scala.reflect.{ClassTag, classTag}

abstract class Script(cs: CommandService, val engine: Engine) extends ControlDsl {
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

  def load(path: Path): Script = synchronized {
    ammonite.Main().runScript(path, Seq.empty)
    val constructor = tag.runtimeClass.getConstructors.toList.head
    constructor.newInstance(null, null).asInstanceOf[Script]
  }

  def init[T <: Script : ClassTag]: Unit = {
    tag = classTag[T]
  }
}
