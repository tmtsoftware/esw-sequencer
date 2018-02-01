package old

import java.io.PrintWriter

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter._

/** A simple example showing programmatic usage of the REPL. */
object Interpreter extends App {
  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true
  settings.processArgumentString("-deprecation -feature -Xfatal-warnings -Xlint")

  //  the interpreter is used by the javax.script engine
  val intp = new IMain(settings, new PrintWriter(Console.out, true))
  def interpret(code: String): Unit = {
    import Results._
    val res = intp.interpret(code) match {
      case Success => "OK!"
      case _       => "Sorry, try again."
    }
  }

  interpret(
    """
      |def run() = {
      |println("hello, world")
      |val who = "world"
      |println(("hello", who))
      |}
      |""".stripMargin
  )

}
