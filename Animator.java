/*
	The purpose of this class is to make it easier for students to animate JavaFX applications.
	It calls the provided method approximately 60 times per second.  The method needs
	to have a single parameter of type 'long'.  For example:

		//This next line calls public void handle(long time){} on this object, 60 times per second.
		Animator.start(this::handle);

		//This next line calls public void sail(long time){} on boat, 60 times per second.
		Animator.start(boat::sail);

	If you want to stop or restart the animation later on, you can use a variable like this:
		Animator a = Animator.start(clock::moveHands);
	Then you can call start and stop on it, such as:
		a.stop();

	This is all based on AnimationTimer, which is built into JavaFX:
		https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html

	The syntax for using AnimationTimer directly is a little more cumbersome than Animator, but
	it is still easy to use.
*/

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

