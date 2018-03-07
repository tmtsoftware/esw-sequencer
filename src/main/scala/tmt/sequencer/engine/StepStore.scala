package tmt.sequencer.engine

import tmt.sequencer.{Command, Id}

case class StepStore(steps: List[Step]) { outer =>

  //query

  def hasNext: Boolean   = steps.exists(_.isPending) && !isPaused
  def isPaused: Boolean  = next.exists(_.hasBreakpoint)
  def next: Option[Step] = steps.find(_.isPending)

  //update

  def replace(id: Id, command: Command): StepStore      = replace(id, Step.from(command))
  private def replace(id: Id, command: Step): StepStore = insertAfter(id, command).delete(id)

  def prepend(command: Command): StepStore = insert(_.isPending, Step.from(command))
  def append(command: Command): StepStore  = copy(steps = steps :+ Step.from(command))

  def delete(id: Id): StepStore = {
    val (pre, post) = steps.span(_.command.id != id)
    copy(steps = pre ::: post.tail)
  }

  def insertAfter(id: Id, command: Command): StepStore      = insertAfter(id, Step.from(command))
  private def insertAfter(id: Id, command: Step): StepStore = insert(_.id == id, command)

  private def insert(after: Step => Boolean, engineCommand: Step): StepStore = {
    val (pre, post) = steps.span(x => !after(x))
    val newCommands = pre ::: post.headOption.toList ::: (engineCommand :: post.tail)
    copy(steps = newCommands)
  }

  def addBreakpoint(id: Id): StepStore                        = transform(id, _.addBreakpoint())
  def removeBreakpoint(id: Id): StepStore                     = transform(id, _.removeBreakpoint())
  def updateStatus(id: Id, stepStatus: StepStatus): StepStore = transform(id, _.withStatus(stepStatus))

  def pause(): StepStore  = next.map(step => transform(step.id, _.addBreakpoint())).flat
  def resume(): StepStore = next.map(step => transform(step.id, _.removeBreakpoint())).flat

  private def transform(id: Id, f: Step => Step): StepStore = {
    steps.find(step => step.id == id && step.isPending).map(step => replace(id, f(step))).flat
  }

  private implicit class StepOps(optStep: Option[StepStore]) {
    def flat: StepStore = optStep.getOrElse(outer)
  }
}
