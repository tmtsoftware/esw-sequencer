import $file.iris_dark_night
import tmt.sequencer.ScriptImports.Script
import tmt.sequencer.dsl.CswServices

object IrisFactory {
  def get(cs: CswServices): Script = cs.observingMode match {
    case "darknight"  => new iris_dark_night.IrisDarkNight(cs)
    case "clearskies" => ???
  }
}
