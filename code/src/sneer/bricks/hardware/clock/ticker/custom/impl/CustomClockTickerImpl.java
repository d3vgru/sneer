package sneer.bricks.hardware.clock.ticker.custom.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.cpu.threads.Threads;

class CustomClockTickerImpl implements CustomClockTicker {

	private final Threads _threads = my(Threads.class);
	private final Clock _clock = my(Clock.class);

	@Override
	public void start(final int millisToSleep) {
		tick(millisToSleep);
		_threads.startStepping(new Runnable() { @Override public void run() {
			tick(millisToSleep);
		}});
	}

	@Override
	public void start(final int millisToSleep, final long timeIncrement) {
		tick(millisToSleep, timeIncrement);
		_threads.startStepping(new Runnable() { @Override public void run() {
			tick(millisToSleep, timeIncrement);
		}});
	}

	private void tick(int millisToSleep) {
		_clock.advanceTime(System.currentTimeMillis());
		_threads.sleepWithoutInterruptions(millisToSleep);
	}

	private void tick(int millisToSleep, long timeIncrement) {
		_clock.advanceTime(System.currentTimeMillis() + timeIncrement);
		_threads.sleepWithoutInterruptions(millisToSleep);
	}

}