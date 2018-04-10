import tmt.sequencer.ScriptImports._
import $file.^.iris.iris_factory
import tmt.sequencer.models.AggregateResponse

class OcsDarkNight(cs: CswServices) extends Script(cs) {

  val iris = iris_factory.IrisFactory.get(cs)

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

  handleCommand("setup-iris") { command =>
    spawn {
      val maybeCommand = cs.nextIf(c2 => c2.name == "setup-iris").await
      val response1 = if (maybeCommand.isDefined) {
        val command2 = maybeCommand.get
        val subCommand2 = command2.copy(id = Id("B1"), parentId = command2.id)
        iris.execute(subCommand2).await
      } else {
        AggregateResponse
      }

      println(s"[Ocs] Command received - ${command.name}")
      val subCommand1 = command.copy(id = Id("A1"), parentId = command.id)
      val response2 = iris.execute(subCommand1).await
      val response = response1.add(response2)
      println(s"[Ocs] Received response")
      response
    }
  }

  handleCommand("setup-iris2") { command =>
    spawn {
      val aggregateResponse = iris.execute(command).await
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
