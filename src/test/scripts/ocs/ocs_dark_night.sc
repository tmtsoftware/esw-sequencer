import tmt.sequencer.ScriptImports._
import tmt.sequencer.models.EngineMsg.SequencerEvent
import $file.^.iris.iris_factory

import scala.concurrent.duration.DurationDouble

class OcsDarkNight(cs: CswServices) extends Script(cs) {

  override def observingMode = "DarkNight"

  val iris = iris_factory.IrisFactory.get(observingMode, cs)

  var eventCount = 0
  var commandCount = 0

  val subscription = cs.subscribe("ocs") { event =>
    eventCount = eventCount + 1
    println(event)
  }

  val cancellable = cs.publish(5.seconds) {
    SequencerEvent("metadata", (eventCount + commandCount).toString)
  }

  override def execute(command: Command): Future[CommandResults] = spawn {
    commandCount += 1
    println("[Ocs] command received")
    if (command.name == "setup-iris") {
      iris.execute(command).await
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
