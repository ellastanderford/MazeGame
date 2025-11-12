/*
	The purpose of this class is to make it easier to animate JavaFX applications.
	It calls the provided method approximately 60 times per second. */

import javafx.animation.*;
public class Animator extends AnimationTimer {
	private TimerStub runner;

	private Animator() {}

	private Animator(TimerStub runner) {
		this.runner = runner;
	}

	public void handle(long time) {
		runner.handle(time);
	}

	public static Animator start(TimerStub t) {
		Animator a = new Animator(t);
		a.start();
		return a;
	}
}

interface TimerStub {
	public void handle(long time);
}

