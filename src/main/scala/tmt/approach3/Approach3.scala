package tmt.approach3

import java.nio.file.{Path, Paths}

import ammonite.ops.RelPath
import tmt.sequencer.Wiring
import tmt.services.Command

object Approach3 extends App {

  val relativePath: Path = args match {
    case Array(p) => Paths.get(p)
    case _        => Paths.get("scripts/OcsSequencer.sc")
  }

  private val path = ammonite.ops.pwd / RelPath(relativePath)

  val wiring = new Wiring
  import wiring._

//  RemoteRepl.server(wiring).start()

  engine.pushAll(List(Command("setup-assembly1", List(1, 2))))
  val script = ScriptImports.load(path, commandService)
  scriptRunner.run(script)
}
