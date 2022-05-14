package es.eriktorr.notifications_engine
package infrastructure

import domain.MessageDispatcher

import cats.effect.{IO, Ref}

final case class DispatchedMessagesState(messages: List[Message])

object DispatchedMessagesState:
  def empty: DispatchedMessagesState = DispatchedMessagesState(List.empty)

final class FakeMessageDispatcher(stateRef: Ref[IO, DispatchedMessagesState])
    extends MessageDispatcher:
  override def dispatch(message: Message): IO[Unit] =
    stateRef.update(currentState => currentState.copy(message :: currentState.messages))
