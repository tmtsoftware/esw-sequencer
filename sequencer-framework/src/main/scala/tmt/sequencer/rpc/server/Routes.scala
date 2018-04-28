package tmt.sequencer.rpc.server

import scala.concurrent.{ExecutionContext, Future}
import sloth._

import boopickle.Default._
import chameleon.ext.boopickle._
import java.nio.ByteBuffer

import cats.implicits._
//import _root_.tmt.sequencer.rpc.serde.TypeMappings._
//import tmt.sequencer.rpc.serde.ScalaPbChameleon._

import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}

class Routes(sequenceFeeder: SequenceFeeder, sequenceEditor: SequenceEditor)(implicit ec: ExecutionContext) {
  val value: Router[ByteBuffer, Future] = Router[ByteBuffer, Future]
    .route[SequenceFeeder](sequenceFeeder)
    .route[SequenceEditor](sequenceEditor)
}
