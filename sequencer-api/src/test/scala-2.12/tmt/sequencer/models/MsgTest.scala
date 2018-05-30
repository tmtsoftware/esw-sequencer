package tmt.sequencer.models

import org.scalatest.FunSuite
import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

class MsgTest extends FunSuite {
  test("demo") {
    val foo1    = Foo("hello")
    val jsonFoo = foo1.asJson.noSpaces
    println(jsonFoo)
    println(decode[Foo](jsonFoo))
  }
}

case class Foo(value: String)
