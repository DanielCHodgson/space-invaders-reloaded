package invaders.entity;

/**
 * A read-only simulation object; its collision dimensions do not depend on its artwork.
 * Implementations must be immutable because render snapshots share these values.
 */
public interface Entity {
    int x();

    int y();

    int width();

    int height();

    default Bounds bounds() {
        return new Bounds(x(), y(), width(), height());
    }
}
