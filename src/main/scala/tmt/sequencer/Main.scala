package tmt.sequencer

import ammonite.ops
import ammonite.ops.{Path, RelPath}
import tmt.sequencer.models.{Command, Id}

object Main extends App {
  val scriptFile = args.headOption.getOrElse("scripts/OcsSequencer.sc")
  val path: Path = ops.pwd / RelPath(scriptFile)

  val wiring = new Wiring(path)
  import wiring._

  supervisorRef

  sequencer.addAll(List(Command(Id("command0"), "setup-assembly1", List(1, 2))))

  remoteRepl.server().start()
}
