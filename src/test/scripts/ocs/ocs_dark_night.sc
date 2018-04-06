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
        val responses2 = iris.executeToBeDeleted(command2).await
        Some(CommandResponse.Composite(command2.id, command2.parentId, responses2))
      } else {
        None
      }

      println(s"[Ocs] Command received - ${command.name}")
      val responses: Set[CommandResponse] = iris.executeToBeDeleted(command).await
      println(s"[Ocs] Result received - ${command.name} with responses - $responses")
      val composite = CommandResponse.Composite(command.id, command.parentId, responses)
      AggregateResponse.single(composite).add(maybeComposite)
    }
  }

  handleCommand("setup-iris2") { command =>
    spawn {
      val responses = iris.executeToBeDeleted(command).await
      println(s"[Ocs2] Result received - ${command.name} with responses - $responses")
      println("*" * 50)
      AggregateResponse.single(CommandResponse.Composite(command.id, command.parentId, responses))
    }
  }


  override def onShutdown(): Future[Done] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
    Done
  }
}
