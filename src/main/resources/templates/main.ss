import tmt.sequencer.Script
import tmt.sequencer.Dsl

class MainScript extends Script {
  val dsl: Dsl = Dsl.build()
  import dsl._

  def run(command: Int): Unit = {
    <script>
  }
}

scala.reflect.classTag[MainScript].runtimeClass
