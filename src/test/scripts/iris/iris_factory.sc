import $file.^.framework.observing_modes
import $file.iris_dark_night
import observing_modes.ObservingMode
import observing_modes.ObservingMode._
import tmt.sequencer.ScriptImports.{CswServices, Script}

object IrisFactory {
  def get(cs: CswServices): Script = ObservingMode.withNameInsensitive(cs.observingMode) match {
    case DarkNight  => new iris_dark_night.IrisDarkNight(cs)
    case ClearSkies => ???
  }
}
