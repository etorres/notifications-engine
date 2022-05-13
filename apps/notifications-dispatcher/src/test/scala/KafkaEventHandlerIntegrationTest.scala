package es.eriktorr.notifications_engine

import Generators.{eventIdGen, messageGen}
import KafkaEventHandlerIntegrationTest.testCaseGen

import cats.effect.IO
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.effect.PropF.{effectOfPropFToPropF, forAllF}
import org.scalacheck.{Gen, Test}

final class KafkaEventHandlerIntegrationTest extends CatsEffectSuite with ScalaCheckEffectSuite:
  override def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(1).withWorkers(1)

  test("it should handle events") {
    forAllF(testCaseGen) { testCase =>
      IO.unit.map(_ => fail("not implemented"))
    }
  }

object KafkaEventHandlerIntegrationTest:
  final private case class TestCase(message: Message, eventId: EventId)

  private val testCaseGen: Gen[TestCase] = for
    message <- messageGen
    eventId <- eventIdGen
  yield TestCase(message, eventId)
