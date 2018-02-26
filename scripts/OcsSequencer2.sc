import tmt.approach3.Script._

init[OcsSequencer2]

class OcsSequencer2(cs: CommandService, engine: Engine) extends Script(cs, engine) {
  override def onCommand(x: Command): Unit = {
    println("command")
  }

  override def onShutdown(): Unit = {
    println("shutdown2")
  }
}
