package es.eriktorr.notifications_engine
package infrastructure

import io.confluent.kafka.schemaregistry.avro.AvroSchema
import io.confluent.kafka.schemaregistry.client.{CachedSchemaRegistryClient, SchemaRegistryClient}

object KafkaSchemaRegistryClient extends EventAvroCodec:
  @main def hello(): Unit =
    val maybeSchema = eventAvroCodec.schema
    maybeSchema.map { schema =>
      val avroSchema = AvroSchema(schema)

      val schemaRegistryClient = CachedSchemaRegistryClient("http://localhost:8081", 5)
      val result = schemaRegistryClient.register("notifications-engine-tests-value", avroSchema)
      println(result)
    }
