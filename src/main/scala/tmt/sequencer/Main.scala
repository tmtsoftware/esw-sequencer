package tmt.sequencer

import ammonite.ops.{Path, RelPath}
import tmt.sequencer.models.{Command, Id}
import tmt.sequencer.util.ScriptRepo

object Main extends App {
  val scriptFile = args.headOption.getOrElse("scripts/ocs_sequencer.sc")

  private val basePath = "/tmp/gitRepo"

  private val remote = "https://github.com/Poorva17/try1.git"

  ScriptRepo.clone(remote, basePath)

  val path: Path = Path(basePath) / RelPath(scriptFile)

  val wiring = new Wiring(path)
  import wiring._

  supervisorRef

  sequencer.addAll(List(Command(Id("command0"), "setup-iris", List(1, 2))))
  sequencer.addAll(List(Command(Id("command1"), "setup-tcs", List(10, 20))))

  println("")

  remoteRepl.server().start()
}
