package tmt.sequencer.models

case class StepStore(steps: List[Step]) { outer =>

  //query

  def hasNext: Boolean   = next.isDefined && !isPaused
  def isPaused: Boolean  = next.exists(_.hasBreakpoint)
  def next: Option[Step] = steps.find(_.isPending)

  //update

  def replace(id: Id, commands: List[Command]): StepStore        = replaceSteps(id, Step.from(commands))
  private def replaceSteps(id: Id, steps: List[Step]): StepStore = insertStepsAfter(id, steps).delete(Set(id))

  def prepend(commands: List[Command]): StepStore = insert(_.isPending, Step.from(commands))
  def append(commands: List[Command]): StepStore  = copy(steps ::: Step.from(commands))

  def delete(ids: Set[Id]): StepStore = copy(steps.filterNot(step => ids.contains(step.id) && step.isPending))

  def insertAfter(id: Id, commands: List[Command]): StepStore        = insertStepsAfter(id, Step.from(commands))
  private def insertStepsAfter(id: Id, steps: List[Step]): StepStore = insert(_.id == id, steps)

  private def insert(after: Step => Boolean, newSteps: List[Step]): StepStore = {
    val (pre, post) = steps.span(x => !after(x))
    copy(pre ::: post.headOption.toList ::: newSteps ::: post.tail)
  }

  def reset: StepStore = copy(steps.filterNot(_.isPending))

  def addBreakpoints(ids: List[Id]): StepStore                = transformAll(ids, _.addBreakpoint())
  def removeBreakpoints(ids: List[Id]): StepStore             = transformAll(ids, _.removeBreakpoint())
  def updateStatus(id: Id, stepStatus: StepStatus): StepStore = transform(id, _.withStatus(stepStatus))

  def pause: StepStore  = next.map(step => transform(step.id, _.addBreakpoint())).flat
  def resume: StepStore = next.map(step => transform(step.id, _.removeBreakpoint())).flat

  private def transformAll(ids: List[Id], f: Step => Step): StepStore = ids.foldLeft(this) { (store, id) =>
    store.transform(id, f)
  }

  private def transform(id: Id, f: Step => Step): StepStore = {
    val maybeStep = steps.find(step => step.id == id && step.isPending)
    maybeStep.map(step => replaceSteps(id, List(f(step)))).flat
  }

  private implicit class StepOps(optStep: Option[StepStore]) {
    def flat: StepStore = optStep.getOrElse(outer)
  }
}

object StepStore {
  def from(commands: List[Command]): StepStore = StepStore(Step.from(commands))
  def empty                                    = StepStore(List.empty)
}
