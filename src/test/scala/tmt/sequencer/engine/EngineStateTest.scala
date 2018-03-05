package tmt.sequencer.engine

import org.scalatest.FunSuite
import tmt.sequencer.Command

import scala.collection.immutable.Queue

class EngineStateTest extends FunSuite {

  test("should return status response if queue is empty") {
    val emptyQueue       = Queue.empty
    val emptyList        = List.empty
    val engineState      = EngineState(emptyQueue)
    val expectedResponse = StatusResponse(emptyList, emptyList, emptyList)

    val actualResponse = engineState.statusQuery()

    assert(expectedResponse === actualResponse)
  }

  test("should return status response if queue has processed, inprogress, remaining commands") {
    val processedCommand  = Command("processed-setup", List(1, 2), CommandStatus.Processed)
    val inProgressCommand = Command("inprogressd-setup", List(1, 2), CommandStatus.InProgress)
    val remainingCommand  = Command("remaining-setup", List(1, 2))
    val commands          = List(processedCommand, inProgressCommand, remainingCommand)
    val queue             = Queue.empty
    val engineState       = EngineState(queue.enqueue(commands))

    val expectedResponse = StatusResponse(List(processedCommand),
                                          List(inProgressCommand),
                                          List(Command("remaining-setup", List(1, 2), CommandStatus.Remaining)))

    val actualResponse = engineState.statusQuery()

    assert(expectedResponse === actualResponse)
  }

  test("should return status response if queue has only remaining commands") {
    val remainingCommand1 = Command("remaining-setup1", List(1, 2))
    val remainingCommand2 = Command("remaining-setup2", List(1, 2))
    val commands          = List(remainingCommand1, remainingCommand2)
    val queue             = Queue.empty
    val engineState       = EngineState(queue.enqueue(commands))

    val expectedResponse =
      StatusResponse(List.empty, List.empty, commands)

    val actualResponse = engineState.statusQuery()

    assert(expectedResponse === actualResponse)
  }
}
