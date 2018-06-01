package tmt.sequencer.api

import scala.language.higherKinds

trait Streaming2[S[_]] {
  def from(a: Int): S[String]
}

object Streaming2 {
  val ApiName = "Streaming2"
  val From    = "from"
}
