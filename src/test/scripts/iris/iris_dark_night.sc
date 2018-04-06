import tmt.sequencer.ScriptImports._
import tmt.sequencer.models.AggregateResponse

class IrisDarkNight(cs: CswServices) extends Script(cs) {

  var eventCount = 0
  var commandCount = 0

  handleCommand("setup-iris") { command =>
    spawn {
      println(s"\n[Iris] Command received - ${command.name}")
      val firstAssemblyResponse = cs.setup("iris-assembly1", command).await
      val commandFailed = firstAssemblyResponse.isInstanceOf[CommandResponse.Failed]

      val restAssemblyResponses = if (commandFailed) {
        Set(cs.setup("iris-assembly2", command).await)
      } else {
        par(
          cs.setup("iris-assembly3", command),
          cs.setup("iris-assembly4", command)
        ).await
      }

      val composite = CommandResponse.Composite(command.id, command.parentId, restAssemblyResponses + firstAssemblyResponse)
      println(s"\n[Iris] Result received - ${command.name} with result - $composite")
      AggregateResponse.single(composite)
    }
  }
}
