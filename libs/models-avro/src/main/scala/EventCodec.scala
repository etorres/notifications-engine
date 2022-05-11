package es.eriktorr.notification_engine

import cats.syntax.all.*
import vulcan.Codec

object EventCodec:

  implicit val eventCodec: Codec[Event] =
    implicit val eventIdCodec: Codec[EventId] = EventIdCodec.eventIdCodec
    implicit val channelCodec: Codec[Channel] = ChannelCodec.channelCodec
    implicit val payloadCodec: Codec[Payload] = PayloadCodec.payloadCodec

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
