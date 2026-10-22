package invaders.asset;

/** A sprite's source rectangles and animation timing, independent of simulation dimensions. */
public record SpriteDefinition(
        String image,
        int x,
        int y,
        int frameWidth,
        int frameHeight,
        int frames,
        int stepX,
        int stepY,
        int ticksPerFrame) {
    public SpriteDefinition {
        if (image == null
                || image.isBlank()
                || x < 0
                || y < 0
                || frameWidth < 1
                || frameHeight < 1
                || frames < 1
                || stepX < 0
                || stepY < 0
                || ticksPerFrame < 1)
            throw new IllegalArgumentException("Invalid sprite definition");
    }
}
