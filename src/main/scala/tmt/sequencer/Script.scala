package tmt.sequencer

import scala.io.Source
import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

trait Script {
  def run(command: Int): Unit
}

trait ScriptFactory {
  def make(dsl: Dsl): Script
}

object ScriptFactory {
  def fromString(code: String): ScriptFactory = {
    val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()
    val testClass = tb.compile(tb.parse(code))().asInstanceOf[Class[_]]
    testClass.getDeclaredConstructor().newInstance().asInstanceOf[ScriptFactory]
  }

  def fromFile(name: String): ScriptFactory = fromString {
    val template = Source.fromResource("templates/main.ss").mkString
    val script = Source.fromResource(name).mkString
    template.replace("<script>", script)
  }
}
