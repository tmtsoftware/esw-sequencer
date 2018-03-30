import tmt.sequencer.ScriptImports._

class IrisDarkNight(cs: CswServices) extends Script(cs) {

  var eventCount = 0
  var commandCount = 0

  handleCommand("setup-iris") { command =>
    spawn {
      println(s"\n[Iris] Command received - ${command.name}")
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

        val finalResults = commandResults.prepend(commandResult)
        println(s"\n[Iris] Result received - ${command.name} with result - $finalResults")
        finalResults
      }
    }
}
