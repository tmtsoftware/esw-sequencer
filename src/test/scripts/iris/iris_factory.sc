import $file.^.framework.observing_modes
import $file.iris_dark_night
import observing_modes.ObservingMode
import observing_modes.ObservingMode._
import tmt.sequencer.ScriptImports.CswServices
import tmt.sequencer.dsl.Script

class IrisFactory(cs: CswServices) {
  def get(name: String): Script = ObservingMode.withNameInsensitive(name) match {
    case DarkNight => new iris_dark_night.IrisDarkNight(cs, name)
    case ClearSkies => ???
  }
}
