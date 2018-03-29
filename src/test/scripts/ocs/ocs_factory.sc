import $file.^.framework.observing_modes
import $file.ocs_dark_night
import observing_modes.ObservingMode
import observing_modes.ObservingMode._
import tmt.sequencer.ScriptImports.CswServices
import tmt.sequencer.dsl.Script


class OcsFactory(cs: CswServices) {
  def get(name: String): Script = ObservingMode.withNameInsensitive(name) match {
    case DarkNight => new ocs_dark_night.OcsDarkNight(cs, name)
    case ClearSkies => ???
  }
}
