package es.eriktorr.notifications_engine

import Generators.{eventGen, messageGen}

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*

final class MessageAvroCodecUnitTest extends ScalaCheckSuite with EventAvroCodec:

  property("message encoding is reversible") {
    forAll(messageGen) { message =>
      assertEquals(
        for
          schema <- messageAvroCodec.schema
          payload <- messageAvroCodec.encode(message)
          result <- messageAvroCodec.decode(payload, schema)
        yield result,
        Right(message),
      )
    }
  }

  property("event encoding is reversible") {
    forAll(eventGen) { event =>
      assertEquals(
        for
          schema <- eventAvroCodec.schema
          payload <- eventAvroCodec.encode(event)
          result <- eventAvroCodec.decode(payload, schema)
        yield result,
        Right(event),
      )
    }
  }
