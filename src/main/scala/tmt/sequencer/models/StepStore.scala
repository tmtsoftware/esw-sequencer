package tmt.sequencer.models

case class StepStore(steps: List[Step]) { outer =>

  //query

  def hasNext: Boolean   = steps.exists(_.isPending) && !isPaused
  def isPaused: Boolean  = next.exists(_.hasBreakpoint)
  def next: Option[Step] = steps.find(_.isPending)

  //update

  def replace(id: Id, commands: List[Command]): StepStore           = copy(steps = replaceStep(id, Step.from(commands)))
  private def replaceStep(id: Id, commands: List[Step]): List[Step] = insertAfterStep(id, commands).delete(id)

  def prepend(commands: List[Command]): StepStore = insert(_.isPending, Step.from(commands))
  def append(commands: List[Command]): StepStore  = copy(steps = steps ::: Step.from(commands))

  private def delete(id: Id): List[Step] = {
    val (pre, post) = steps.span(_.command.id != id)
    pre ::: post.tail
  }

  def delete(ids: List[Id]): StepStore = {
    copy(steps = ids.flatMap(delete))
  }

  def insertAfter(id: Id, commands: List[Command]): StepStore = insertAfterStep(id, Step.from(commands))
  private def insertAfterStep(id: Id, commands: List[Step])   = insert(_.id == id, commands)

  private def insert(after: Step => Boolean, newSteps: List[Step]) = {
    val (pre, post) = steps.span(x => !after(x))
    val newCommands = pre ::: post.headOption.toList ::: newSteps ::: post.tail
    copy(steps = newCommands)
  }

  def addBreakpoints(ids: List[Id]): StepStore                = copy(steps = ids.flatMap(id => transform(id, _.addBreakpoint())))
  def removeBreakpoints(ids: List[Id]): StepStore             = copy(steps = ids.flatMap(id => transform(id, _.removeBreakpoint())))
  def updateStatus(id: Id, stepStatus: StepStatus): StepStore = copy(steps = transform(id, _.withStatus(stepStatus)))

  def pause(): StepStore  = next.map(step => copy(steps = transform(step.id, _.addBreakpoint()))).flat
  def resume(): StepStore = next.map(step => copy(transform(step.id, _.removeBreakpoint()))).flat

  private def transform(id: Id, f: Step => Step): List[Step] = {
    steps.find(step => step.id == id && step.isPending) match {
      case Some(step) => replaceStep(id, List(f(step)))
      case None       => steps
    }

  }

  private implicit class StepOps(optStep: Option[StepStore]) {
    def flat: StepStore = optStep.getOrElse(outer)
  }
}

object StepStore {
  def from(commands: List[Command]): StepStore = StepStore(Step.from(commands))
  def empty                                    = StepStore(List.empty)
}
