import javafx.animation.*;

public class Animator extends AnimationTimer {
    private static Animator instance;
    private static TimerStub runner;

    private Animator() {}

    public static void start(TimerStub r) {
        runner = r;
        if (instance == null) {
            instance = new Animator();
            instance.start();
        }
    }

    @Override
    public void handle(long time) {
        if (runner != null) {
            runner.handle(time);
        }
    }
}