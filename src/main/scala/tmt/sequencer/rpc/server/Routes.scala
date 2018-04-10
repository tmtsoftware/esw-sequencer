package tmt.sequencer.rpc.server

import tmt.sequencer.rpc.api.{SequenceManager, SequenceProcessor}

import scala.concurrent.{ExecutionContext, Future}
import sloth._
import boopickle.Default._
import chameleon.ext.boopickle._
import java.nio.ByteBuffer

import cats.implicits._

class Routes(sequenceProcessor: SequenceProcessor, sequenceManager: SequenceManager)(implicit ec: ExecutionContext) {
  val value: Router[ByteBuffer, Future] =
    Router[ByteBuffer, Future].route[SequenceProcessor](sequenceProcessor).route[SequenceManager](sequenceManager)
}
