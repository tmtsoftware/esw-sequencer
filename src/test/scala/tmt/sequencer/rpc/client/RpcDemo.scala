package tmt.sequencer.rpc.client

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.Wiring
import tmt.sequencer.models.{Command, Id}

class RpcDemo extends FunSuite with BeforeAndAfterAll {

  private val wiring = new Wiring("ocs", "darknight", false)
  import wiring._
  engine.start(sequencer, script)
  rpcServer.start(9090).get

  override protected def afterAll(): Unit = {
    system.terminate().get
  }

  test("abc") {
    val response = rpcClient.sequenceProcessor.submit(List(Command(Id("command1"), "setup-iris", List(1, 2, 3, 4)))).get
    println("----------->" + response)
  }
}
