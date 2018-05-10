package tmt.sequencer.models

import upickle.default._

case class Boo(x: Int)

class CommandTest extends org.scalatest.FunSuite {
  test("demo") {
    val command = Command(Id("1"), "setup-iris", List(1, 2))
    println("**********************")
    println(read[Command](write(command)))
    println("**********************")

  }

  test("boo") {
    println(read[Boo](write(Boo(100))))
  }
}
