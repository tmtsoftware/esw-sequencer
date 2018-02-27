package tmt.approach3

import java.nio.file.{Path, Paths}

import ammonite.ops
import ammonite.ops.RelPath
import tmt.sequencer.Wiring
import tmt.services.Command

object Approach3 extends App {

  val wiring = new Wiring
  import wiring._

  val relativePath: Path = args match {
    case Array(p) => Paths.get(p)
    case _        => Paths.get("scripts/OcsSequencer.sc")
  }

  val path = ops.pwd / RelPath(relativePath)

  scriptRunner.run(path)

  engine.pushAll(List(Command("setup-assembly1", List(1, 2))))

  //  RemoteRepl.server(wiring).start()
}
