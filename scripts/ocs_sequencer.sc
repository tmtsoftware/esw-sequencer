import tmt.sequencer.ScriptImports._
import $file.iris_helper
import $file.tcs_helper
import $file.helpers
import scala.collection.mutable

init[OcsSequencer]

class OcsSequencer(cs: CommandService) extends Script(cs) {

  var results: mutable.Buffer[CommandResult] = mutable.Buffer.empty
  val iris = new iris_helper.Iris(cs)
  val tcs = new tcs_helper.Tcs(cs)

  override def onSetup(command: Command): CommandResult = {
    if (command.name == "setup-iris") {
      println("*" * 50)
      val result = iris.onSetup(command)
      results += result
      Thread.sleep(10000)
      println(results)
      println("")
      println("")
      result
    } else if (command.name == "setup-tcs") {
      println("*" * 50)
      val result = tcs.onSetup(command)
      results += result
      Thread.sleep(10000)
      println(results)
      println("")
      println("")
      result
    }
    else {
      println(s"unknown command=$command")
      CommandResult.Empty
    }
  }


  override def onObserve(x: Command): CommandResult = {
    println("observe")
    val x = helpers.square(99)
    println(x)
    CommandResult.Single(x.toString)
  }

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onEvent(event: SequencerEvent): Unit = {
    println(event)
  }
}
