package tmt.sequencer

import ammonite.ops
import ammonite.ops.{Path, RelPath}

object Main extends App {
  val scriptFile = args.headOption.getOrElse("scripts/OcsSequencer.sc")
  val path: Path = ops.pwd / RelPath(scriptFile)

  val wiring = new Wiring(path)
  import wiring._

  supervisorRef

  engine.pushAll(List(Command("setup-assembly1", List(1, 2))))

  remoteRepl.server().start()
}
