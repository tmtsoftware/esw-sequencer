package tmt.sequencer.rpc.server.spike

import akka.stream.scaladsl.Source
import tmt.sequencer.api.Streaming2
import tmt.sequencer.rpc.server.spike.A.S

object Streaming2Impl extends Streaming2[A.S] {
  override def from(a: Int): S[String] = Source.fromIterator(() => Iterator.from(a).map(_.toString))
}

object A {
  type S[x] = Source[x, Any]
}
