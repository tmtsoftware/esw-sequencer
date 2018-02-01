package example

object Program extends App {
  ScriptFactory.fromFile("simple.ss").build().run()
}
