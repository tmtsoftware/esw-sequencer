package example

import scala.io.Source
import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

object Program extends App {

  // load script
  val source = Source.fromResource("simple.ss").mkString

  //compile script
  val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()
  val testClass = tb.compile(tb.parse(source))().asInstanceOf[Class[_]]

  //load class
  val testClassConstructor = testClass.getDeclaredConstructors()(0)

  //instantiate
  val instance: Factory = testClassConstructor.newInstance().asInstanceOf[Factory]

  //method call
  instance.create().run()
}
