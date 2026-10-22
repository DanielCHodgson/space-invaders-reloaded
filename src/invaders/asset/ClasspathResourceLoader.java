package invaders.asset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Packaged resources first, then the source tree for direct IDE launches. */
public final class ClasspathResourceLoader implements ResourceLoader {
    @Override
    public InputStream open(String name) throws IOException {
        InputStream resource = getClass().getResourceAsStream("/" + name);
        if (resource != null) return resource;
        Path relative = Path.of(name.startsWith("Music/") ? "src" : "res").resolve(name);
        Path location;
        try {
            location =
                    Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            throw new IOException("Cannot locate resources", e);
        }
        for (Path root = location.toAbsolutePath(); root != null; root = root.getParent()) {
            Path candidate = root.resolve(relative);
            if (Files.isRegularFile(candidate)) return Files.newInputStream(candidate);
        }
        if (Files.isRegularFile(relative)) return Files.newInputStream(relative);
        throw new IOException("Missing resource: " + name);
    }
}
