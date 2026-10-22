package invaders.loop;

public interface GameLoop extends AutoCloseable {
    void start();

    @Override
    void close();
}
