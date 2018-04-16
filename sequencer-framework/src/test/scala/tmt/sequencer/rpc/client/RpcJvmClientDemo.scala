package tmt.sequencer.rpc.client

import monix.reactive.Observable
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.Wiring
import tmt.sequencer.models.{Command, Id}

class RpcJvmClientDemo extends FunSuite with BeforeAndAfterAll {

  private val wiring = new Wiring("ocs", "darknight", None, false)
  import wiring._
  engine.start(sequencer, script)
  rpcServer.start().get

  override protected def afterAll(): Unit = {
    system.terminate().get
  }

  test("sequencer") {
    val rpcClient    = new JvmSequencerClient("http://0.0.0.0:9090")
    val ocsProcessor = rpcClient.sequenceProcessor
    val ocsManager   = rpcClient.sequenceManager

    val response = ocsProcessor.submitSequence(List(Command(Id("command1"), "setup-iris", List(1, 2, 3, 4)))).get
    println("----------->" + response)

    val sequence = ocsManager.sequence.get
    println("----------->" + sequence)
  }

  test("streaming") {
    val client = new JvmStreamingClient("ws://0.0.0.0:9090/ws")
    import monix.execution.Scheduler.Implicits.global

    client.events.flatMap(xs => Observable.fromIterable(xs)).foreach(println)

    client.streaming.from(78).onComplete { res =>
      println(s"Got response: $res")
    }
  }
}
