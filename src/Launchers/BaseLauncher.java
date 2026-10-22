package Launchers;

import invaders.app.GameApplication;

/** Preserves the original IntelliJ entry point. */
public final class BaseLauncher {
    private BaseLauncher() {}

    public static void main(String[] args) {
        GameApplication.launch();
    }
}
