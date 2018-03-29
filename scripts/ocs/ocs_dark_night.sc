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
    println(s"[Received OCS]: ------------------> event=${event.value} on key=${event.key}")
  }

  val cancellable = cs.publish(5.seconds) {
    SequencerEvent("ocs-metadata", (eventCount + commandCount).toString)
  }

  override def execute(command: Command): Future[CommandResults] = spawn {
    commandCount += 1
    println("\n\n" + "*" * 50)
    println(s"[Ocs] Command received - ${command.name}")
    if (command.name == "setup-iris") {
      val result = iris.execute(command).await
      println(s"\n[Ocs] Result received - ${command.name} with result - ${result}")
      println("\n\n" + "*" * 50)
      result
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
