package example

import scala.io.Source
import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

trait ScriptFactory {
  def build(): Script
}

object ScriptFactory {
  def fromString(content: String): ScriptFactory = {
    val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()
    val testClass = tb.compile(tb.parse(content))().asInstanceOf[Class[_]]
    testClass.getDeclaredConstructor().newInstance().asInstanceOf[ScriptFactory]
  }

  def fromFile(name: String): ScriptFactory = fromString {
    val template = Source.fromResource("template.ss").mkString
    val script = Source.fromResource(name).mkString
    template.replace("<script>", script)
  }
}
