package tmt.approach3

import java.nio.file.Paths

import ammonite.ops.Path

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationDouble

object Approach3 extends App {

  private val path1 = "/Users/poorav/TMT/spikes/sequencer-spike/scripts/OcsSequencer.sc"
  private val path2 = "/Users/poorav/TMT/spikes/sequencer-spike/scripts/OcsSequencer2.sc"

  val fs = List(
    Future(Script.load(Path(Paths.get(path1)))),
    Future(Script.load(Path(Paths.get(path2))))
  )

  Await.result(Future.sequence(fs), 15.seconds).foreach(_.onShutdown())
}
