package tmt.sequencer

import java.io.File

import tmt.sequencer.ScriptFactory.fromFile

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
  def fromFileName(name: String): ScriptFactory = fromFile {
    new File(getClass.getClassLoader.getResource(name).toURI)
  }

  def fromFilePath(path: String): ScriptFactory = fromFile {
    new File(path)
  }

  def fromFile(file: File): ScriptFactory = fromString {
    val template = Source.fromResource("templates/main.ss").mkString
    val script = Source.fromFile(file).mkString
    template.replace("<script>", script)
  }

  def fromString(code: String): ScriptFactory = {
    val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()
    val testClass = tb.compile(tb.parse(code))().asInstanceOf[Class[_]]
    testClass.getDeclaredConstructor().newInstance().asInstanceOf[ScriptFactory]
  }
}
