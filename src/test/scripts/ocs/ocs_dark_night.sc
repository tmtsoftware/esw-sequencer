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
      println("*" * 50)
      val maybeCommand = cs.nextIf(c2 => c2.name == "setup-iris").await

      val maybeComposite = if (maybeCommand.isDefined) {
        val command2 = maybeCommand.get
        iris.executeToBeDeleted(command2).await
      } else {
        Set.empty[CommandResponse.Composite]
      }

      println(s"[Ocs] Command received - ${command.name}")
      val responses: Set[CommandResponse.Composite] = iris.executeToBeDeleted(command).await
      println(s"[Ocs] Result received - ${command.name} with responses - $responses")
      AggregateResponse(responses).add(maybeComposite)
    }
  }

  handleCommand("setup-iris2") { command =>
    spawn {
      val responses = iris.executeToBeDeleted(command).await
      println(s"[Ocs2] Result received - ${command.name} with responses - $responses")
      println("*" * 50)
      AggregateResponse(responses)
    }
  }

  override def onShutdown(): Future[Done] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
    Done
  }
}
