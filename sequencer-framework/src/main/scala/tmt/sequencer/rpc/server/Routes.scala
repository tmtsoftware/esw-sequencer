package tmt.sequencer.rpc.server

import scala.concurrent.{ExecutionContext, Future}
import sloth._

import upickle.default._
import chameleon.ext.upickle._

import cats.implicits._

import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}

class Routes(sequenceFeeder: SequenceFeeder, sequenceEditor: SequenceEditor)(implicit ec: ExecutionContext) {
  val value: Router[String, Future] = Router[String, Future]
    .route[SequenceFeeder](sequenceFeeder)
    .route[SequenceEditor](sequenceEditor)
}
