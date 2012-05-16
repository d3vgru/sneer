package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import static org.junit.Assert.assertArrayEquals;

import java.net.DatagramPacket;
import java.util.Arrays;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Consumer;

public final class LoggingSender implements Consumer<DatagramPacket> {
	
	private Register<String> packetHistory = my(Signals.class).newRegister("");
	private SetRegister<String> packetHistorySet = my(CollectionSignals.class).newSetRegister();

	@Override public void consume(DatagramPacket packetToSend) {
		byte[] bytes = packetToSend.getData();
		byte[] seal = Arrays.copyOf(bytes, Seal.SIZE_IN_BYTES);
		byte[] payload = payload(bytes, Seal.SIZE_IN_BYTES);
		assertArrayEquals(ownSealBytes(), seal);
		String current = packetHistory.output().currentValue();
		String packet = "| " + toString(payload) + ",to:" + packetToSend.getAddress().getHostAddress() + ",port:" + packetToSend.getPort();
		packetHistory.setter().consume(current + packet);
		packetHistorySet.add(packet);
	}

	private String toString(byte[] payload) {
		String ret = new String(payload);
		return ret.isEmpty() ? "<empty>" : ret;
	}

	public Signal<String> history() {
		return packetHistory.output();
	}

	private static byte[] payload(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}

	private static byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

	public SetSignal<String> historySet() {
		return packetHistorySet.output();
	}

}