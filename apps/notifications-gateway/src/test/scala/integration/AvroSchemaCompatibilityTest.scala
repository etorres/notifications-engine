package es.eriktorr.notification_engine
package integration

import config.KafkaConfig
import infrastructure.TestFilters.online

import fs2.kafka.vulcan.SchemaRegistryClientSettings
import fs2.kafka.vulcan.testkit.SchemaSuite
import munit.CatsEffectSuite
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityType
import vulcan.Codec

final class AvroSchemaCompatibilityTest
    extends CatsEffectSuite
    with SchemaSuite
    with EventAvroCodec:
  private[this] val checker = compatibilityChecker(
    SchemaRegistryClientSettings(KafkaConfig.default.schemaRegistry.value),
  )

  override def munitFixtures: Seq[Fixture[?]] = List(checker)

  test("event codec should be compatible".tag(online)) {
    checker()
      .checkReaderCompatibility(eventAvroCodec, s"${KafkaConfig.default.topic}-value")
      .map(compatibility =>
        assertEquals(
          compatibility.getType(),
          SchemaCompatibilityType.COMPATIBLE,
          compatibility.getResult().nn.getIncompatibilities(),
        ),
      )
  }
