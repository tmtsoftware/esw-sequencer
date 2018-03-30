import tmt.sequencer.ScriptImports._

class IrisDarkNight(cs: CswServices) extends Script(cs) {

  var eventCount = 0
  var commandCount = 0

  val subscription = cs.subscribe("iris") { event =>
    eventCount = eventCount + 1
    println(s"[Received Iris]: ------------------> event=${event.value} on key=${event.key}")
  }

  val cancellable = cs.publish(seconds(5)) {
    val totalCount = eventCount + commandCount
    SequencerEvent("iris-metadata", totalCount.toString)
  }

  val commandHandler = { command =>
    spawn {
      commandCount += 1
      println(s"[Iris] Command received - ${command.name}")
      if (command.name == "setup-iris") {
        val commandResult = cs.setup("iris-assembly1", command).await
        val commandFailed = commandResult.isInstanceOf[CommandResult.Failed]

        val commandResults = if (commandFailed) {
          CommandResults.from(cs.setup("iris-assembly2", command).await)
        } else {
          CommandResults(
            par(
              cs.setup("iris-assembly3", command),
              cs.setup("iris-assembly4", command)
            ).await
          )
        }

        val finalResults = commandResults.addResult(commandResult)
        println(s"\n[Iris] Result received - ${command.name} with result - ${finalResults}")
        finalResults
      } else {
        println(s"unknown command=$command")
        CommandResults.empty
      }
    }
  }

  override def onShutdown(): Future[Unit] = spawn {
    subscription.shutdown()
    cancellable.cancel()
    println("shutdown")
  }
}
