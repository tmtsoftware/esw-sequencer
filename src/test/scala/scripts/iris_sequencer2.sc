import reactify.Var
import tmt.sequencer.ScriptImports._

init[IrisSequencer2]

class IrisSequencer2(cs: CommandService) extends Script(cs) {

  val resultCount = Var(0)
  val eventCount = Var(0)

  override def onSetup(command: Command): Unit = {
    if (command.name == "setup-iris") {
      val topResult = cs.setup2("iris-assembly1", command)
      topResult.attach { v =>
        Thread.sleep(2000)
        resultCount := resultCount + 1
        val resultCh = if(v.isInstanceOf[CommandResult.Failed]) {
          Thread.sleep(2000)
          cs.setup2("iris-assembly2", command)
        } else {
          cs.setup2("iris-assembly3", command)
        }
        resultCh.attach { res =>
          resultCount := resultCount + 1
          cs.complete(res)
        }
      }

    } else {
      println(s"unknown command=$command")
    }
  }

  override def onEvent(event: SequencerEvent): Unit = {
    eventCount := eventCount + 1
    println(event)
  }

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onCommandCompletion(command: Command, commandResult: CommandResult): Unit = {}

}
