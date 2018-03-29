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
  def get(sequencerId: String, observingMode: String, cs: CswServices): Script = {
    SequencerId.withNameInsensitive(sequencerId) match {
      case Ocs  => new ocs_factory.OcsFactory(cs).get(observingMode)
      case Iris => new iris_factory.IrisFactory(cs).get(observingMode)
      case Tcs  => ???
    }
  }
}
