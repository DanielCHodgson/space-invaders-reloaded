package invaders.asset;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface ResourceLoader {
    InputStream open(String path) throws IOException;
}
