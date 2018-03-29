import $file.^.framework.observing_modes
import $file.ocs_dark_night
import observing_modes.ObservingMode
import observing_modes.ObservingMode._
import tmt.sequencer.ScriptImports.CswServices
import tmt.sequencer.dsl.Script


object OcsFactory {
  def get(observingMode: String, cs: CswServices): Script = ObservingMode.withNameInsensitive(observingMode) match {
    case DarkNight => new ocs_dark_night.OcsDarkNight(cs)
    case ClearSkies => ???
  }
}
