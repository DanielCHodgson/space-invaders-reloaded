package invaders.loop;

import invaders.config.GameRules;

import javax.swing.Timer;

/**
 * Swing scheduler adapter. Every update, input callback and paint runs on the same event thread.
 */
public final class SwingGameLoop implements GameLoop {
    private final Timer timer;
    private final FixedStepClock clock =
            new FixedStepClock(GameRules.TICKS_PER_SECOND, GameRules.MAX_CATCH_UP_STEPS);
    private final Runnable update;
    private final Runnable present;
    private long previousTime;

    public SwingGameLoop(Runnable update, Runnable present) {
        this.update = update;
        this.present = present;
        timer = new Timer(1000 / GameRules.TICKS_PER_SECOND, event -> frame());
    }

    private void frame() {
        long now = System.nanoTime();
        int steps = clock.advance(now - previousTime);
        previousTime = now;
        for (int i = 0; i < steps; i++) update.run();
        present.run();
    }

    @Override
    public void start() {
        if (timer.isRunning()) return;
        clock.reset();
        previousTime = System.nanoTime();
        timer.start();
    }

    @Override
    public void close() {
        timer.stop();
        clock.reset();
    }
}
