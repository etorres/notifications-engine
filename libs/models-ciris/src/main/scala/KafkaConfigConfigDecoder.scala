package es.eriktorr.notifications_engine

trait KafkaConfigConfigDecoder
    extends BootstrapServerConfigDecoder
    with ConsumerGroupConfigDecoder
    with NonEmptyListConfigDecoder
    with SchemaRegistryConfigDecoder
    with TopicConfigDecoder
