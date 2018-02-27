package tmt.approach3

import ammonite.ops.Path
import ammonite.util.Res.Exception
import tmt.approach3

import scala.reflect.{classTag, ClassTag}

object ScriptImports {
  @volatile
  private var tag: ClassTag[_] = _

  type Script         = approach3.Script
  type CommandService = tmt.sequencer.CommandService
  type Command        = tmt.services.Command
  val Command = tmt.services.Command

  private[tmt] def load(path: Path, cs: CommandService): Script = synchronized {
    println(path)
    ammonite.Main().runScript(path, Seq.empty) match {
      case (Exception(t, msg), _) =>
        println(s"script loading failed due to: $msg")
        t.printStackTrace()
      case (x, _) =>
        println(s"script loading status: $x")
    }
    val constructor = tag.runtimeClass.getConstructors.toList.head
    constructor.newInstance(cs).asInstanceOf[Script]
  }

  def init[T <: Script: ClassTag]: Unit = {
    tag = classTag[T]
  }
}
