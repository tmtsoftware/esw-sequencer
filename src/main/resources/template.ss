import tmt.sequencer.Script
import tmt.sequencer.Dsl

class SimpleScript extends Script {
  val dsl: Dsl = Dsl.build()
  import dsl._

  def run(command: Int): Unit = {
    <script>
  }
}

scala.reflect.classTag[SimpleScript].runtimeClass
