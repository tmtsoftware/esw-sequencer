package tmt.approach3

import ammonite.ops
import ammonite.ops.{Path, RelPath}
import tmt.sequencer.Wiring
import tmt.services.Command

object Approach3 extends App {

  val wiring = new Wiring
  import wiring._

  val scriptFile = args.headOption.getOrElse("scripts/OcsSequencer.sc")
  val path: Path = ops.pwd / RelPath(scriptFile)

  scriptRunner.run(path)

  engine.pushAll(List(Command("setup-assembly1", List(1, 2))))

  //  RemoteRepl.server(wiring).start()
}
