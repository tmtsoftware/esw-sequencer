import tmt.sequencer.ScriptImports._
import tmt.sequencer.dsl
import tmt.sequencer.models.AggregateResponse

class OcsDarkNight(cs: dsl.CswServices) extends Script(cs) {

  val iris = cs.sequenceProcessor("iris")

  var eventCount = 0
  var commandCount = 0

  val subscription = cs.subscribe("ocs") { event =>
    eventCount = eventCount + 1
    println(s"[Received OCS]: ------------------> event=${event.value} on key=${event.key}")
    Done
  }

  val cancellable = cs.publish(6.seconds) {
    SequencerEvent("ocs-metadata", (eventCount + commandCount).toString)
  }

  cs.handleCommand("setup-iris") { command =>
    spawn {
      val maybeCommand = cs.nextIf(c2 => c2.name == "setup-iris").await
      val response1 = if (maybeCommand.isDefined) {
        val command2 = maybeCommand.get
        val subCommand2 = command2.copy(id = Id("B1"))
        iris.submitSequence(List(subCommand2)).await.markSuccessful(command2)
      } else {
        AggregateResponse
      }

      println(s"[Ocs] Command received - ${command.name}")
      val subCommand1 = command.copy(id = Id("A1"))
      val response2 = iris.submitSequence(List(subCommand1)).await.markSuccessful(command)
      val response = response1.add(response2)
      println(s"[Ocs] Received response")
      response
    }
  }

  cs.handleCommand("setup-iris2") { command =>
    spawn {
      val aggregateResponse = iris.submitSequence(List(command)).await.markSuccessful(command)
      println(s"[Ocs2] Result received - ${command.name} with aggregateResponse - $aggregateResponse")
      aggregateResponse
    }
  }

  override def onShutdown(): Future[Done] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
    Done
  }
}
