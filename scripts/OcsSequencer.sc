import tmt.approach3.Script._
import $file.helpers

init[OcsSequencer]

class OcsSequencer(cs: CommandService, engine: Engine) extends Script(cs, engine) {

  override def onCommand(x: Command): Unit = {
    println("command")
  }

  override def onShutdown(): Unit = {
    println("shutdown")
    println(helpers.square(43))
  }

}
