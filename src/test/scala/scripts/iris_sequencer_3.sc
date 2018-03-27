import tmt.sequencer.Fiber._
import tmt.sequencer.ScriptImports._

import scala.concurrent.Future

init[IrisSequencer3]

class IrisSequencer3(cs: CommandService) extends Script(cs) {

  var resultCount = 0
  var eventCount = 0

  override def onSetup(command: Command): Future[CommandResult] = async {
    if (command.name == "setup-iris") {
      val topR = await(cs.setup3("iris-assembly1", command))
      Thread.sleep(2000)
      resultCount = resultCount + 1
      val result = if (topR.isInstanceOf[CommandResult.Failed]) {
        Thread.sleep(2000)
        await(cs.setup3("iris-assembly2", command))
      } else {
        await(cs.setup3("iris-assembly3", command))
      }
      resultCount = resultCount + 1
      CommandResult.Multiple(List(topR, result))
    } else {
      println(s"unknown command=$command")
      CommandResult.Empty
    }
  }

  override def onEvent(event: SequencerEvent): Future[Unit] = async {
    eventCount = eventCount + 1
    println(event)
  }

  override def onShutdown(): Future[Unit] = async {
    println("shutdown")
  }

  override def onCommandCompletion(command: Command, commandResult: CommandResult): Unit = {}

}
