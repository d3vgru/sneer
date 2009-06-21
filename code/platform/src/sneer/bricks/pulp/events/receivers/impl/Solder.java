package sneer.bricks.pulp.events.receivers.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;

public class Solder<T> {

	private final Consumer<? super T> _delegate;

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;

	public Solder(EventSource<? extends T> eventSource, Consumer<? super T> receiver) {
		_delegate = receiver;

		_referenceToAvoidGc = my(Signals.class).receive(eventSource, new Consumer<T>() { @Override public void consume(T event) {
			_delegate.consume(event);
		}});
	}
}