package tmt.sequencer

import org.scalatest.Matchers
import ScriptImports._

class DslTest extends org.scalatest.FunSuite with Matchers {

  def x: Int = {
    println("blocking")
    10
  }

  test("dd") {
    val dsl = new Dsl(null)
    println(dsl.par(x, x))
  }
}
