import $file.^.framework.observing_modes
import $file.iris_dark_night
import observing_modes.ObservingMode
import observing_modes.ObservingMode._
import tmt.sequencer.ScriptImports.CswServices
import tmt.sequencer.dsl.Script

object IrisFactory {
  def get(observingMode: String, cs: CswServices): Script = ObservingMode.withNameInsensitive(observingMode) match {
    case DarkNight => new iris_dark_night.IrisDarkNight(cs)
    case ClearSkies => ???
  }
}
