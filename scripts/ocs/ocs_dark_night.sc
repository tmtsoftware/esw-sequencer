import tmt.sequencer.ScriptImports._
import $file.^.iris.iris_factory


class OcsDarkNight(cs: CswServices) extends Script(cs) {

  val iris = iris_factory.IrisFactory.get(cs)

  var eventCount = 0
  var commandCount = 0

  val subscription = cs.subscribe("ocs") { event =>
    eventCount = eventCount + 1
    println(s"[Received OCS]: ------------------> event=${event.value} on key=${event.key}")
  }

  val cancellable = cs.publish(seconds(5)) {
    SequencerEvent("ocs-metadata", (eventCount + commandCount).toString)
  }

  handleCommand("setup-iris") { command =>
    spawn {
      val result = iris.execute(command).await
      println(s"\n[Ocs] Result received - ${command.name} with result - $result")
      println("\n\n" + "*" * 50)
      result
    }
  }

  handleCommand("setup-iris2") { command =>
    spawn {
      val result = iris.execute(command).await
      println(s"\n[Ocs2] Result received - ${command.name} with result - $result")
      println("\n\n" + "*" * 50)
      result
    }
  }


  override def onShutdown(): Future[Unit] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
  }
}
