import $file.^.framework.observing_modes
import $file.ocs_dark_night
import observing_modes.ObservingMode
import observing_modes.ObservingMode._
import tmt.sequencer.ScriptImports.{CswServices, Script}


object OcsFactory {
  def get(cs: CswServices): Script = ObservingMode.withNameInsensitive(cs.observingMode) match {
    case DarkNight  => new ocs_dark_night.OcsDarkNight(cs)
    case ClearSkies => ???
  }
}
