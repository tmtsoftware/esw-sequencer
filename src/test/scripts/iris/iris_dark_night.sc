import tmt.sequencer.ScriptImports._
import tmt.sequencer.models.AggregateResponse

class IrisDarkNight(cs: CswServices) extends Script(cs) {

  var eventCount = 0
  var commandCount = 0

  handleCommand("setup-iris") { command =>
    spawn {
      println(s"[Iris] Command received - ${command.name}")
      val firstAssemblyResponse = cs.setup("iris-assembly1", command.copy(id = Id(s"${command.id}a"))).await
      val commandFailed = firstAssemblyResponse.isInstanceOf[CommandResponse.Failed]

      val restAssemblyResponses = if (commandFailed) {
        Set(cs.setup("iris-assembly2", command.copy(id = Id(s"${command.id}d"))).await)
      } else {
        par(
          cs.setup("iris-assembly3", command.copy(id = Id(s"${command.id}b"))),
          cs.setup("iris-assembly4", command.copy(id = Id(s"${command.id}c")))
        ).await
      }

      val response = AggregateResponse.add(firstAssemblyResponse).add(restAssemblyResponses).markSuccessful(command)
      println(s"[Iris] AggregateResponse: $response")
      response
    }
  }
}
