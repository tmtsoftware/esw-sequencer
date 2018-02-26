package tmt

object AA {
  val x    = 100
  var y: T = _

  type T = {
    def square(x: Int): Int
  }

  def init(t: T): Unit = {
    y = t
  }
}
