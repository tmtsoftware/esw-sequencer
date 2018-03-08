package tmt.sequencer.models

case class Sequence(steps: List[Step]) { outer =>

  //query

  def hasNext: Boolean   = next.isDefined && !isPaused
  def isPaused: Boolean  = next.exists(_.hasBreakpoint)
  def next: Option[Step] = steps.find(_.isPending)

  //update

  def replace(id: Id, commands: List[Command]): Sequence        = replaceSteps(id, Step.from(commands))
  private def replaceSteps(id: Id, steps: List[Step]): Sequence = insertStepsAfter(id, steps).delete(Set(id))

  def prepend(commands: List[Command]): Sequence = insert(_.isPending, Step.from(commands))
  def append(commands: List[Command]): Sequence  = copy(steps ::: Step.from(commands))

  def delete(ids: Set[Id]): Sequence = copy(steps.filterNot(step => ids.contains(step.id) && step.isPending))

  def insertAfter(id: Id, commands: List[Command]): Sequence        = insertStepsAfter(id, Step.from(commands))
  private def insertStepsAfter(id: Id, steps: List[Step]): Sequence = insert(_.id == id, steps)

  private def insert(after: Step => Boolean, newSteps: List[Step]): Sequence = {
    val (pre, post) = steps.span(x => !after(x))
    copy(pre ::: post.headOption.toList ::: newSteps ::: post.tail)
  }

  def reset: Sequence = copy(steps.filterNot(_.isPending))

  def addBreakpoints(ids: List[Id]): Sequence                = transformAll(ids, _.addBreakpoint())
  def removeBreakpoints(ids: List[Id]): Sequence             = transformAll(ids, _.removeBreakpoint())
  def updateStatus(id: Id, stepStatus: StepStatus): Sequence = transform(id, _.withStatus(stepStatus))

  def pause: Sequence  = next.map(step => transform(step.id, _.addBreakpoint())).flat
  def resume: Sequence = next.map(step => transform(step.id, _.removeBreakpoint())).flat

  private def transformAll(ids: List[Id], f: Step => Step): Sequence = ids.foldLeft(this) { (store, id) =>
    store.transform(id, f)
  }

  private def transform(id: Id, f: Step => Step): Sequence = {
    val maybeStep = steps.find(step => step.id == id && step.isPending)
    maybeStep.map(step => replaceSteps(id, List(f(step)))).flat
  }

  private implicit class StepOps(optStep: Option[Sequence]) {
    def flat: Sequence = optStep.getOrElse(outer)
  }
}

object Sequence {
  def from(commands: List[Command]): Sequence = Sequence(Step.from(commands))
  def empty                                   = Sequence(List.empty)
}
