package tmt.sequencer.engine

import org.scalatest.FunSuite
import tmt.sequencer.Command

class EngineStateTest extends FunSuite {

  test("should return all the processed commands") {

    val processedCommand = Command("processed-setup", List(1, 2)), CommandStatus.Processed, Position(1))
    val commands = List(
      processedCommand,
      Command("remaining-setup", List(1, 2)), CommandStatus.Remaining, Position(2))
    )
    val engineState      = EngineState.from(commands)
    val expectedResponse = List(processedCommand)

    val actualResponse = engineState.processed

    assert(expectedResponse === actualResponse)
  }

  test("should insert command at position 4") {

    val processedCommand  = Command("processed-setup", List(1, 2))
    val inProgressCommand = Command("inprogress-setup", List(1, 2))
    val remainingCommand1 = Command("remaining-setup", List(1, 2))
    val remainingCommand2 = Command("remaining-setup", List(1, 2))

    val commands = List(
      processedCommand,
      inProgressCommand,
      remainingCommand1,
      remainingCommand2
    )

    val positionToUpsert  = Position(4)
    val commandToUpsert   = EngineCommand(Command("upcoming-command", List(1, 2)), CommandStatus.Remaining, positionToUpsert)
    val actualEngineState = EngineState.from(commands).upsert(commandToUpsert)
    val expectedEngineState =
      EngineState.from(List(processedCommand, inProgressCommand, remainingCommand1, Command("upcoming-command", List(1, 2))))

    assert(actualEngineState === expectedEngineState)
  }
}
