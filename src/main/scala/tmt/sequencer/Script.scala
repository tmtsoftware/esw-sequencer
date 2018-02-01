package tmt.sequencer

import scala.io.Source
import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

trait Script {
  def run(command: Int): Unit
}

object Script {
  def fromString(code: String): Script = {
    val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()
    val testClass = tb.compile(tb.parse(code))().asInstanceOf[Class[_]]
    testClass.getDeclaredConstructor().newInstance().asInstanceOf[Script]
  }

  def fromFile(name: String): Script = fromString {
    val template = Source.fromResource("templates/main.ss").mkString
    val script = Source.fromResource(name).mkString
    template.replace("<script>", script)
  }
}
