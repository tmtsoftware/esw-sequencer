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
      var ee: Set[CommandResponse.Composite] = null
//      val dd = cs.nextIf(c2 => c2.name == "setup-iris").await
//      if(dd.isDefined) {
//        ee = iris.execute(dd.get).await
//      }

      println(s"[Ocs] Command received - ${command.name}")
      val composites: Set[CommandResponse.Composite] = iris.execute(command).await
      println(s"[Ocs] Result received - ${command.name} with composites - $composites")
      AggregateResponse(composites ++ ee)
    }
  }

  handleCommand("setup-iris2") { command =>
    spawn {
      val composites = iris.execute(command).await
      println(s"[Ocs2] Result received - ${command.name} with composites - $composites")
      println("*" * 50)
      AggregateResponse(composites)
    }
  }


  override def onShutdown(): Future[Done] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
    Done
  }
}
