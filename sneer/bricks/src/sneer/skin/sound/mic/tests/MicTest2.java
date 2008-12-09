package sneer.skin.sound.mic.tests;

import static wheel.lang.Environments.my;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import sneer.pulp.tuples.TupleSpace;
import sneer.skin.sound.PcmSoundPacket;
import sneer.skin.sound.kernel.Audio;
import sneer.skin.sound.mic.Mic;
import tests.Contribute;
import tests.JMockContainerEnvironment;
import wheel.lang.Consumer;
import wheel.lang.Threads;

public class MicTest2 extends MicTestBase {

	private static Mic _subject = my(Mic.class);

	private final Mockery _mockery = new JUnit4Mockery();
	@Contribute private final Audio _audio = _mockery.mock(Audio.class);
	private final TargetDataLine _line = _mockery.mock(TargetDataLine.class);
	private final AudioFormat _format = new AudioFormat(8000, 16, 1, true, false);

	
	
	@Test (timeout = 1000)
	public void testChannels() {
		_mockery.checking(soundExpectations());

		int defaultChannel = 0;

		_subject.open();
		waitForPcmPacketChannel(defaultChannel);

		_subject.setChannel(42);
		waitForPcmPacketChannel(42);
	}

	
	private void waitForPcmPacketChannel(final int channel) {
		final Object notifier = new Object();

		synchronized (notifier) { //Refactor Try to extract this "wait for notify" logic.
			my(TupleSpace.class).addSubscription(PcmSoundPacket.class, new Consumer<PcmSoundPacket>() { @Override public void consume(PcmSoundPacket packet) {
				if (packet.channel != channel) return;
				synchronized (notifier) { notifier.notify(); }
			}});
			
			Threads.waitWithoutInterruptions(notifier);
		}
	}


	private Expectations soundExpectations() {
		return new Expectations() {{
			//Sequence main = _mockery.sequence("main");
			one(_audio).tryToOpenCaptureLine(); will(returnValue(_line));// inSequence(main);
			allowing(_audio).defaultAudioFormat(); will(returnValue(_format));
			allowing(_line).read(with(aNonNull(byte[].class)), with(0), with(320)); will(returnValue(320));
			allowing(_line).close();// inSequence(main);
		}};
	}

}