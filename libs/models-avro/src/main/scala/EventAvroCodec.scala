package es.eriktorr.notification_engine

import cats.syntax.all.*
import vulcan.Codec

trait EventAvroCodec extends EventIdAvroCodec with ChannelAvroCodec with PayloadAvroCodec:
  implicit val eventAvroCodec: Codec[Event] =
    Codec.record(
      name = "Event",
      namespace = AvroNamespaces.default,
    ) { field =>
      (
        field("id", _.id),
        field("channel", _.channel),
        field("payload", _.payload),
      ).mapN(Event(_, _, _))
    }
