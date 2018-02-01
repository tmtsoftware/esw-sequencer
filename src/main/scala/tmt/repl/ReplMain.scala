package tmt.repl

object ReplMain extends App {
  val hello = "Hello"
  // Break into debug REPL with
  ammonite.Main(
      predefCode =
        """
          |repl.frontEnd() = ammonite.repl.FrontEnd.JLineUnix
          |println("Starting Debugging!")
          |import tmt.sequencer.Dsl
          |val dsl: Dsl = Dsl.build()
          |import dsl._
        """.stripMargin

  ).run(
    "hello" -> hello,
    "fooValue" -> foo()
  )

  def foo() = 1
}
