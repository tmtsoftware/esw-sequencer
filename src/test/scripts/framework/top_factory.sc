import $file.^.iris.iris_factory
import $file.^.ocs.ocs_factory
import tmt.sequencer.ScriptImports
import tmt.sequencer.dsl.{CswServices, Script, ScriptFactory}

ScriptImports.init[TopScriptFactory]

class TopScriptFactory extends ScriptFactory {
  def get(cs: CswServices): Script = cs.sequencerId match {
    case "ocs"  => ocs_factory.OcsFactory.get(cs)
    case "iris" => iris_factory.IrisFactory.get(cs)
    case "tcs"  => ???
  }
}
