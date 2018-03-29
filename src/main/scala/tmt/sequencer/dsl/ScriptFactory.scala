package tmt.sequencer.dsl

import tmt.sequencer.gateway.CswServices

trait ScriptFactory {
  def get(cs: CswServices): Script
}
