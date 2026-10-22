package invaders.asset;

import java.awt.image.BufferedImage;

/** Cached frames selected by simulation time rather than by drawing frequency. */
public final class Animation {
    private final BufferedImage[] frames;
    private final int ticksPerFrame;

    public Animation(BufferedImage[] frames, int ticksPerFrame) {
        if (frames.length == 0 || ticksPerFrame < 1)
            throw new IllegalArgumentException("Invalid animation");
        this.frames = frames.clone();
        this.ticksPerFrame = ticksPerFrame;
    }

    public BufferedImage frame(long ticks) {
        return frames[(int) Math.floorMod(ticks / ticksPerFrame, (long) frames.length)];
    }
}
