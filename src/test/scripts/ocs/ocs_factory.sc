import $file.ocs_dark_night
import tmt.sequencer.ScriptImports.{CswServices, Script}

object OcsFactory {
  def get(cs: CswServices): Script = cs.observingMode match {
    case "darknight"  => new ocs_dark_night.OcsDarkNight(cs)
    case "clearskies" => ???
  }
}
