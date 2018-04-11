import $file.ocs_dark_night
import tmt.sequencer.ScriptImports.Script
import tmt.sequencer.dsl.CswServices

object OcsFactory {
  def get(cs: CswServices): Script = cs.observingMode match {
    case "darknight"  => new ocs_dark_night.OcsDarkNight(cs)
    case "clearskies" => ???
  }
}
