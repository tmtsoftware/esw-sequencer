package tmt.approach3

import java.nio.file.Paths

import ammonite.ops.Path
import tmt.approach3.sequencer.ControlDsl
import tmt.approach3.sequencer.RemoteRepl
import tmt.sequencer.Wiring
import tmt.services.Command

object Approach3 extends App {

  private val path1 = "/Users/poorav/TMT/spikes/sequencer-spike/scripts/OcsSequencer.sc"
  private val path2 = "/Users/poorav/TMT/spikes/sequencer-spike/scripts/OcsSequencer2.sc"

  val wiring = new Wiring
  val engine = wiring.engine

  RemoteRepl.server(wiring).start()

  engine.push(Command("setup-assembly1", List(1,2)))

/*
    val fs = List(
    Future(Script.load(Path(Paths.get(path1)), wiring.commandService, wiring.engine)),
    Future(Script.load(Path(Paths.get(path2)), wiring.commandService, wiring.engine))
  )

  Await.result(Future.sequence(fs), 15.seconds).foreach(_.onShutdown())
*/

  val script = Script.load(Path(Paths.get(path1)), wiring.commandService, wiring.engine)

//  forEach(script.onCommand(_))

  script.onShutdown()

}
