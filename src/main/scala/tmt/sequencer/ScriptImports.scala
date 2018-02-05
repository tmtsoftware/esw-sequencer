package tmt.sequencer

object ScriptImports {
  private[tmt] val wiring = new Wiring

  lazy val Sync: Dsl = wiring.dsl
  lazy val Async: wiring.dsl.Async.type = wiring.dsl.Async
  lazy val E: Engine = wiring.engine
}
