package csw.messages.commands

import akka.actor.typed.ActorRef
import csw.messages.params.models.Id

/**
 * Represents the state of a command execution
 *
 * @param commandStatus the current command status
 * @param subscribers the subscriber list for the change in state
 */
private[csw] case class CommandState(commandStatus: CommandStatus, subscribers: Set[ActorRef[CommandResponse]]) {

  /**
   * Add a new subscriber for change in state
   *
   * @param subscriber the subscriber as an actor to which the updated state will be sent
   * @return a new CommandState instance with updated subscribers
   */
  def addSubscriber(subscriber: ActorRef[CommandResponse]): CommandState = copy(subscribers = subscribers + subscriber)

  /**
   * Remove a subscriber for change in state
   *
   * @param subscriber the subscriber as an actor to which the updated state will be sent
   * @return a new CommandState instance with updated subscribers
   */
  def removeSubscriber(subscriber: ActorRef[CommandResponse]): CommandState =
    copy(subscribers = subscribers - subscriber)

  /**
   * Create a new state from `this` state with the provided command response
   *
   * @param commandResponse the command Response
   * @return a new CommandState instance with the current state as provided command response
   */
  def withCommandStatus(commandResponse: CommandResponse): CommandState =
    copy(commandStatus = this.commandStatus.withCommandResponse(commandResponse))

}

private[csw] object CommandState {

  /**
   * Initaialize the command state for a given command
   *
   * @param runId command identifier as a RunId
   * @param initialState initial command response
   * @return a new command state
   */
  def init(runId: Id, initialState: CommandResponse): CommandState =
    CommandState(CommandStatus(runId, initialState), Set.empty)
}
