package tmt.sequencer.models

case class Sequence(steps: List[Step]) { outer =>

  require(steps.map(_.id).toSet.size == steps.size, "steps can not have duplicate ids")

  //query

  def hasNext: Boolean   = next.isDefined && !isPaused
  def isPaused: Boolean  = next.exists(_.hasBreakpoint)
  def next: Option[Step] = steps.find(_.isPending)

  //update

  def replace(id: Id, commands: List[Command]): Sequence        = replaceSteps(id, Step.from(commands))
  private def replaceSteps(id: Id, steps: List[Step]): Sequence = insertStepsAfter(id, steps).delete(Set(id))

  def prepend(commands: List[Command]): Sequence = {
    val (pre, post) = steps.span(!_.isPending)
    copy(pre ::: Step.from(commands) ::: post)
  }
  def append(commands: List[Command]): Sequence = copy(steps ::: Step.from(commands))

  def delete(ids: Set[Id]): Sequence = copy(steps.filterNot(step => ids.contains(step.id) && step.isPending))

  def insertAfter(id: Id, commands: List[Command]): Sequence = insertStepsAfter(id, Step.from(commands))

  private def insertStepsAfter(id: Id, newSteps: List[Step]): Sequence = {
    val (pre, post) = steps.span(_.id != id)
    copy(pre ::: post.headOption.toList ::: newSteps ::: post.tail)
  }

  def reset: Sequence = copy(steps.filterNot(_.isPending))

  def addBreakpoints(ids: List[Id]): Sequence                = updateAll(ids.toSet, _.addBreakpoint())
  def removeBreakpoints(ids: List[Id]): Sequence             = updateAll(ids.toSet, _.removeBreakpoint())
  def updateStatus(id: Id, stepStatus: StepStatus): Sequence = update(id, _.withStatus(stepStatus))

  def pause: Sequence  = next.map(step => update(step.id, _.addBreakpoint())).flat
  def resume: Sequence = next.map(step => update(step.id, _.removeBreakpoint())).flat

  def update(id: Id, f: Step => Step): Sequence = updateAll(Set(id), f)

  def updateAll(ids: Set[Id], f: Step => Step): Sequence = copy {
    steps.map {
      case step if ids.contains(step.id) => f(step)
      case step                          => step
    }
  }

  private implicit class StepOps(optStep: Option[Sequence]) {
    def flat: Sequence = optStep.getOrElse(outer)
  }
}

object Sequence {
  def empty = Sequence(List.empty)
}
