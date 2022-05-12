package es.eriktorr.notification_engine
package integration

import infrastructure.TestFilters.online

import fs2.kafka.vulcan.SchemaRegistryClientSettings
import fs2.kafka.vulcan.testkit.SchemaSuite
import munit.CatsEffectSuite
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityType
import vulcan.Codec

final class AvroSchemaCompatibilityTest extends CatsEffectSuite with SchemaSuite with EventCodec:
  private[this] val checker = compatibilityChecker(
    SchemaRegistryClientSettings("http://localhost:8081"),
  )

  override def munitFixtures: Seq[Fixture[?]] = List(checker)

  test("event codec should be compatible".tag(online)) {
    checker()
      .checkReaderCompatibility(eventCodec, "notifications-engine-tests-value")
      .map(compatibility =>
        assertEquals(
          compatibility.getType(),
          SchemaCompatibilityType.COMPATIBLE,
          compatibility.getResult().nn.getIncompatibilities(),
        ),
      )
  }
