package tmt.sequencer

import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.{ActorSystem, Scheduler}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.scalatest.FunSuite
import tmt.sequencer.core.Engine
import tmt.sequencer.dsl.Script
import tmt.sequencer.models._

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class EngineTest extends FunSuite {

  private implicit val actorSystem: ActorSystem = ActorSystem("test")
  import actorSystem.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val scheduler: Scheduler            = actorSystem.scheduler
  private implicit val timeout: Timeout                = Timeout(5.seconds)

  val script: Script = new Script(null) {
    override def execute(x: Command): Future[CommandResults] = Future(CommandResults.empty)
    override def onShutdown(): Future[Unit]                  = ???
  }

  test("demo") {
    val stepF  = Future(Step.from(Command(Id("command5"), "setup-iris", List(1, 2, 3, 4))))
    val engine = new Engine(script, null, actorSystem)
//    engine.loop(stepF)
//    engine.loop2()
    Thread.sleep(100000)
  }

}
