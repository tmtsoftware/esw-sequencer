package tmt.approach3

import java.nio.file.{Path, Paths}

import ammonite.ops.RelPath
import tmt.sequencer.Wiring
import tmt.services.Command

object Approach3 extends App {

  val dd: Path = args match {
    case Array(p) => Paths.get(p)
    case _        => Paths.get("scripts/OcsSequencer.sc")
  }

  private val path = ammonite.ops.pwd / RelPath(dd)

  val wiring = new Wiring
  val engine = wiring.engine

//  RemoteRepl.server(wiring).start()

//  engine.pushAll(List(Command("setup-assembly1", List(1, 2))))
  val script = ScriptImports.load(path, wiring.commandService)

  script.onShutdown()
}
