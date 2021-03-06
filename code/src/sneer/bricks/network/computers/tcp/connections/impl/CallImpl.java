package sneer.bricks.network.computers.tcp.connections.impl;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.connections.Call;

class CallImpl implements Call {

	private final String _callerName;
	private final Seal _callerSeal;

	public CallImpl(String callerName, Seal callerSeal) {
		_callerName = callerName;
		_callerSeal = callerSeal;
	}

	@Override
	public String callerName() {
		return _callerName;
	}

	@Override
	public Seal callerSeal() {
		return _callerSeal;
	}

}
