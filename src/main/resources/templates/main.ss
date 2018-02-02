import tmt.sequencer.Script
import tmt.sequencer.ScriptFactory
import tmt.sequencer.Dsl

class MainScript(dsl: Dsl) extends Script {
  import dsl._

  def run(command: Int): Unit = {
    <script>
  }
}

class MainScriptFactory extends ScriptFactory {
  def make(dsl: Dsl): Script = new MainScript(dsl)
}

scala.reflect.classTag[MainScriptFactory].runtimeClass
