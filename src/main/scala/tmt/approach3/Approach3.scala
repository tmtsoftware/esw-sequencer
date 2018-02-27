package tmt.approach3

import tmt.sequencer.Wiring
import tmt.services.Command

object Approach3 extends App {
  private val path = ammonite.ops.pwd / "scripts" / "OcsSequencer.sc"

  val wiring = new Wiring
  val engine = wiring.engine

//  RemoteRepl.server(wiring).start()

//  engine.pushAll(List(Command("setup-assembly1", List(1, 2))))
  val script = ScriptImports.load(path, wiring.commandService)

  script.onShutdown()
}
