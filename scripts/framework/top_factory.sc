import $file.^.iris.iris_factory
import $file.^.ocs.ocs_factory
import $file.observing_modes
import $file.sequencer_ids
import sequencer_ids.SequencerId
import sequencer_ids.SequencerId._
import tmt.sequencer.ScriptImports
import tmt.sequencer.dsl.{Script, ScriptFactory}
import tmt.sequencer.gateway.CswServices

ScriptImports.init[TopScriptFactory]

class TopScriptFactory extends ScriptFactory {
  def get(cs: CswServices): Script = {
    SequencerId.withNameInsensitive(cs.sequencerId) match {
      case Ocs  => ocs_factory.OcsFactory.get(cs)
      case Iris => iris_factory.IrisFactory.get(cs)
      case Tcs  => ???
    }
  }
}
