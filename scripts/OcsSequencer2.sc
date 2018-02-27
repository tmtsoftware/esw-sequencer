import tmt.approach3
import tmt.approach3.ScriptImports._
import tmt.approach3.sequencer

init[OcsSequencer2]

class OcsSequencer2(cs: CommandService, engine: Engine) extends approach3.Script(cs, engine) {
  override def onCommand(x: Command): Unit = {
    println("command")
  }

  override def onShutdown(): Unit = {
    println("shutdown2")
  }
}
