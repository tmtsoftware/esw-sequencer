import tmt.sequencer.ScriptImports._
import tmt.sequencer.models.EngineMsg.SequencerEvent

import scala.concurrent.duration.DurationDouble

class OcsDarkNight(cs: CswServices) extends Script(cs) {

  var eventCount = 0
  var commandCount = 0

  val subscription = cs.subscribe("iris") { event =>
    eventCount = eventCount + 1
    println(event)
  }

  val cancellable = cs.publish(5.seconds) {
    SequencerEvent("metadata", (eventCount + commandCount).toString)
  }

  override def execute(command: Command): Future[CommandResults] = spawn {
    commandCount += 1
    if (command.name == "setup-iris") {
      val topR = cs.setup("iris-assembly1", command).await
      Thread.sleep(2000)
      val result = if (topR.isInstanceOf[CommandResult.Failed]) {
        Thread.sleep(2000)
        List(cs.setup("iris-assembly2", command).await)
      } else {
        par(
          cs.setup("iris-assembly3", command),
          cs.setup("iris-assembly4", command)
        ).await
      }
      val results = CommandResults(topR :: result)
      println(s"final result = $results")
      results
    } else {
      println(s"unknown command=$command")
      CommandResults.empty
    }
  }

  override def onShutdown(): Future[Unit] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
  }
}
