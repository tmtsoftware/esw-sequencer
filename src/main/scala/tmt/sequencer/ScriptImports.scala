package tmt.sequencer

object ScriptImports {
  private[tmt] val wiring = new Wiring

  lazy val D: Dsl = wiring.dsl
  lazy val E: Engine = wiring.engine
}
