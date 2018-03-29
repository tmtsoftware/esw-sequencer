package tmt.sequencer.dsl

import tmt.sequencer.gateway.CswServices

trait ScriptFactory {
  def get(sequencerId: String, observingMode: String, cs: CswServices): Script
}
