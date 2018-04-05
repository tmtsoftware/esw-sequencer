import tmt.sequencer.ScriptImports._
import $file.^.iris.iris_factory

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
      println("\n\n" + "*" * 50)
      var ee: CommandResponse = null
//      val dd = cs.nextIf(c2 => c2.name == "setup-iris").await
//      if(dd.isDefined) {
//        ee = iris.execute(dd.get).await
//      }

      println(s"\n[Ocs] Command received - ${command.name}")
      val irisResponse: CommandResponse = iris.execute(command).await
      println(s"\n[Ocs] Result received - ${command.name} with irisResponse - $irisResponse")
      println("\n\n" + "*" * 50)
      Set(ee, irisResponse)
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


  override def onShutdown(): Future[Done] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
    Done
  }
}
