import reactify.Var
import tmt.sequencer.ScriptImports._

init[IrisSequencer2]

class IrisSequencer2(cs: CommandService) extends Script(cs) {

  val dd: Var[CommandResult] = Var(CommandResult.Empty)

  override def onSetup(command: Command): Unit = {
    if (command.name == "setup-iris") {
      dd := cs.setup2("iris-assembly1", command)
      dd.attach { v =>
        if(v.isInstanceOf[CommandResult.Failed]) {
          dd := cs.setup2("iris-assembly2", command)
        } else {
          dd := cs.setup2("iris-assembly3", command)
        }
      }

    } else {
      println(s"unknown command=$command")
    }
  }

  override def onCommandCompletion(commandResult: CommandResult): Unit = {}

  override def onStepCompletion(commandResult: CommandResult): Unit = {}

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onEvent(event: SequencerEvent): Unit = {
    dd := CommandResult.Single(event.value)
    println(event)
  }
}
