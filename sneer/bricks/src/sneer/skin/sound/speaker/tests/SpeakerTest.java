package sneer.skin.sound.speaker.tests;

import javax.sound.sampled.SourceDataLine;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import sneer.kernel.container.Inject;
import sneer.pulp.clock.Clock;
import sneer.pulp.keymanager.KeyManager;
import sneer.pulp.keymanager.PublicKey;
import sneer.pulp.streams.sequencer.Sequencer;
import sneer.pulp.tuples.TupleSpace;
import sneer.skin.sound.PcmSoundPacket;
import sneer.skin.sound.kernel.Audio;
import sneer.skin.sound.speaker.Speaker;
import tests.Contribute;
import tests.JMockContainerEnvironment;
import tests.TestThatIsInjected;
import wheel.lang.Consumer;
import wheel.lang.ImmutableByteArray;


@RunWith(JMockContainerEnvironment.class)
public class SpeakerTest extends TestThatIsInjected {
	
	@Inject private static Speaker _subject;
	@Inject private static Clock _clock;
	@Inject private static KeyManager _keyManager;
	@Inject private static TupleSpace _tupleSpace;

	private final Mockery _mockery = new JUnit4Mockery();
	private final SourceDataLine _line = _mockery.mock(SourceDataLine.class);
	
	@Contribute private final Audio _audio = _mockery.mock(Audio.class);
	@Contribute private final Sequencer<PcmSoundPacket> _sequencer = _mockery.mock(Sequencer.class);
	
	private Consumer<? super PcmSoundPacket> _consumer;

	@Test
	public void testSilentChannel() throws Exception {
		_mockery.checking(new CommonExpectations());
		
		_subject.open();
		_subject.close();
	}
	
	@Test
	@Ignore
	public void testOnlyTuplesFromContactsGetPlayed() throws Exception {
		_mockery.checking(new SoundExpectations());

		_subject.open();
		_tupleSpace.acquire(p1());
		_tupleSpace.acquire(p2());

		_tupleSpace.acquire(myPacket(new byte[] {-1, 17, 0, 42}));
		
		_subject.close();
	}
	
	@Test
	@Ignore
	public void testTuplesPublishedAfterCloseAreNotPlayed() throws Exception {
		_mockery.checking(new SoundExpectations());
		
		_subject.open();
		
		_tupleSpace.acquire(p1());
		_tupleSpace.acquire(p2());

		_subject.close();
		_tupleSpace.acquire(p1());
	}

	@Test
	@Ignore
	public void testPacketsAreSentToDataLine() throws Exception {
		final Sequence main = _mockery.sequence("main");
		_mockery.checking(new CommonExpectations(){{
			one(_audio).tryToOpenPlaybackLine(); will(returnValue(_line));
			one(_line).write(new byte[]{1, 2, 3, 5}, 0, 4); inSequence(main);
			one(_line).write(new byte[]{7, 11, 13, 17}, 0, 4); inSequence(main);
			one(_line).close(); inSequence(main);
			allowing(_line).isActive(); will(returnValue(true));
		}});
		_subject.open();
		
		_consumer.consume(p1());
		_consumer.consume(p2());

		_subject.close();
	}
	
	class SoundExpectations extends CommonExpectations {
		private final Sequence _mainSequence = _mockery.sequence("main");
		
		SoundExpectations() throws Exception {
			one(_sequencer).sequence(p1(), (short)0); inSequence(_mainSequence);
			one(_sequencer).sequence(p2(), (short)1); inSequence(_mainSequence);
		}
	}

	class CommonExpectations extends Expectations {
//		CommonExpectations() throws Exception {
//			one(_sequencers).createSequencerFor(with(aNonNull(Consumer.class))); will(new CustomAction("keep buffer") { @Override public Object invoke(Invocation invocation) {
//				_consumer = (Consumer<? super PcmSoundPacket>) invocation.getParameter(0);
//				return _sequencer;
//			}});;
//		}
	}

	@SuppressWarnings("deprecation")
	private PublicKey contactKey() {
		return _keyManager.generateMickeyMouseKey("contact");
	}
	
	private PcmSoundPacket myPacket(byte[] pcm) {
		return pcmSoundPacketFor(_keyManager.ownPublicKey(), pcm);
	}

	private PcmSoundPacket contactPacket(byte[] pcm) {
		return pcmSoundPacketFor(contactKey(), pcm);
	}
	
	private PcmSoundPacket pcmSoundPacketFor(PublicKey publicKey, final byte[] pcmPayload) {
		return new PcmSoundPacket(publicKey, _clock.time(), new ImmutableByteArray(pcmPayload, pcmPayload.length));
	}
	
	private PcmSoundPacket p1() {
		return contactPacket(new byte[] { 1, 2, 3, 5 });
	}
	
	private PcmSoundPacket p2() {
		return contactPacket(new byte[] { 7, 11, 13, 17 });
	}
}