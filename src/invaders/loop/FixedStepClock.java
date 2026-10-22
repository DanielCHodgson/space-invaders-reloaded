package invaders.loop;

/**
 * Accumulates elapsed time into bounded fixed simulation steps, independent of any UI scheduler.
 */
public final class FixedStepClock {
    private final long stepNanos;
    private final int maximumSteps;
    private long accumulator;

    public FixedStepClock(int frequency, int maximumSteps) {
        if (frequency < 1 || frequency > 1_000_000_000 || maximumSteps < 1)
            throw new IllegalArgumentException("Invalid clock");
        this.stepNanos = 1_000_000_000L / frequency;
        this.maximumSteps = maximumSteps;
    }

    public int advance(long elapsedNanos) {
        accumulator += Math.clamp(elapsedNanos, 0, stepNanos * maximumSteps);
        int steps = (int) (accumulator / stepNanos);
        accumulator %= stepNanos;
        return steps;
    }

    public void reset() {
        accumulator = 0;
    }
}
