package tmt.sequencer

import ammonite.ops
import ammonite.ops.{Path, RelPath}
import tmt.sequencer.models.{Command, Id}

object Main extends App {
  val scriptFile = args.headOption.getOrElse("scripts/ocs_sequencer.sc")
  val path: Path = ops.pwd / RelPath(scriptFile)

  val wiring = new Wiring(path)
  import wiring._

  supervisorRef

  sequencer.addAll(List(Command(Id("command0"), "setup-iris", List(1, 2))))
  sequencer.addAll(List(Command(Id("command1"), "setup-tcs", List(10, 20))))

  println("")

  remoteRepl.server().start()
}
