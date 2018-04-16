import tmt.sequencer.ScriptImports._

class OcsDarkNight(cs: CswServices) extends Script(cs) {

  val iris = cs.sequenceProcessor("iris")

  var eventCount = 0
  var commandCount = 0

  val subscription = cs.subscribe("ocs") { event =>
    eventCount = eventCount + 1
    println(s"------------------> received-event: ${event.value} on key: ${event.key}")
    Done
  }

  val cancellable = cs.publish(16.seconds) {
    SequencerEvent("ocs-metadata", (eventCount + commandCount).toString)
  }

  cs.handleCommand("setup-iris") { command =>
    spawn {
      val maybeNextCommand = cs.nextIf(c2 => c2.name == "setup-iris").await
      val responseB = if (maybeNextCommand.isDefined) {
        val nextCommand = maybeNextCommand.get
        val subCommandB1 = nextCommand.copy(id = Id("B1"))
        iris.feed(List(subCommandB1)).await.markSuccessful(nextCommand)
      } else {
        AggregateResponse
      }

      println(s"[Ocs] Received command: ${command.name}")
      val subCommandA1 = command.copy(id = Id("A1"))
      val responseA = iris.feed(List(subCommandA1)).await.markSuccessful(command)

      val response = responseA.add(responseB)
      println(s"[Ocs] Received response: $response")
      response
    }
  }

  cs.handleCommand("setup-iris2") { command =>
    spawn {
      println(s"[Ocs2] Received command: ${command.name}")

      val response = iris.feed(List(command)).await.markSuccessful(command)
      println(s"[Ocs2] Received response: $response")
      response
    }
  }

  override def onShutdown(): Future[Done] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
    Done
  }
}
