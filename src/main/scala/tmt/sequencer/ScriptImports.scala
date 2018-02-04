package tmt.sequencer

object ScriptImports {
  private[tmt] val wiring = new Wiring
  import wiring._

  lazy val D: Dsl = dsl
  lazy val E: Engine = engine
}
