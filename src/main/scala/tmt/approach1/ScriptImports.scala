package tmt.approach1

import tmt.sequencer.{Dsl, Engine, Wiring}

object ScriptImports {
  private[tmt] val wiring = new Wiring
  import wiring._

  val D: Dsl = dsl
  val E: Engine = engine
}
