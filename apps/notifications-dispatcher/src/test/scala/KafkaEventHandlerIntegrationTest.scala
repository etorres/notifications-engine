package es.eriktorr.notification_engine

import Generators.{eventIdGen, messageGen}
import KafkaEventHandlerIntegrationTest.testCaseGen

import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.effect.PropF.forAllF
import org.scalacheck.{Gen, Test}

final class KafkaEventHandlerIntegrationTest extends CatsEffectSuite with ScalaCheckEffectSuite:
  override def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(1).withWorkers(1)

  test("it should handle events") {
    forAllF(testCaseGen) { testCase =>
      ???
      ???
      ???
    }
  }

object KafkaEventHandlerIntegrationTest:
  final private case class TestCase(message: Message, eventId: EventId)

  private val testCaseGen: Gen[TestCase] = for
    message <- messageGen
    eventId <- eventIdGen
  yield TestCase(message, eventId)
