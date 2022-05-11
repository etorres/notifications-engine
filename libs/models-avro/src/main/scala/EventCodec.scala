package es.eriktorr.notification_engine

import cats.syntax.all.*
import vulcan.Codec

trait EventCodec extends EventIdCodec with ChannelCodec with PayloadCodec:
  implicit val eventCodec: Codec[Event] =
    Codec.record(
      name = "Event",
      namespace = Namespaces.default,
    ) { field =>
      (
        field("id", _.id),
        field("channel", _.channel),
        field("payload", _.payload),
      ).mapN(Event(_, _, _))
    }
