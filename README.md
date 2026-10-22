# Space Invaders: Reloaded

A bug-fix and refactor of [Space Invaders: Class of '18](https://github.com/DanielCHodgson/space-invaders-class-of-18). Same sprites, sounds and game, with the old lifecycle, collision and threading problems cleaned up.

## Run it

Requires **JDK 21 or newer**, with no Maven, Gradle or extra libraries.

On Windows, from this folder:

```powershell
.\build.ps1 -Run
```

That compiles the source with Java lint warnings treated as errors, runs the regression checks and builds `dist/space-invaders-reloaded.jar`. To build and test without launching, use `.\build.ps1`.

The JAR includes the graphics and audio and can run from any directory:

```text
java -jar path/to/space-invaders-reloaded.jar
```

In IntelliJ, open this folder, choose JDK 21 or newer and run `Launchers.BaseLauncher`.

- **A / D** or **left / right arrows** to move.
- **Space** to shoot; release to fire again.
- Click **Play**, **Help**, **Retry** or **Quit** in the menus.

## Display settings

The startup launcher offers windowed, borderless fullscreen and exclusive fullscreen modes, plus a window size and a launcher visibility checkbox. Borderless fullscreen is the default. Exclusive fullscreen falls back to borderless if the monitor does not support it. The game keeps its aspect ratio at every size.

- **F10** opens settings during play (the game pauses while the dialog is open).
- **F11** cycles display modes; **Alt+Enter** toggles fullscreen/windowed.
- **Esc** returns to windowed mode.

Preferences save automatically to `%LOCALAPPDATA%\SpaceInvadersReloaded\settings.ini`, including window resizing. You can edit it while the game is closed:

```ini
[display]
mode=BORDERLESS
window_width=960
window_height=720

[launcher]
show_at_startup=true
```

`mode` accepts `WINDOWED`, `BORDERLESS` or `FULLSCREEN`. Invalid values use defaults. Delete the file to reset preferences; use F10 to re-enable the launcher if hidden.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the layers and extension points, and [REFACTOR.md](REFACTOR.md) for the fixes and checks. The asset sources were not documented in the old project; this repository does not claim ownership of those graphics or audio.

## Credit

Original game by **Daniel Hodgson**.

## The original README

# Space Invaders: Class of '18

A little Space Invaders game I made while teaching myself Java back in 2018. The first game I ever made and the first app I ever "finished" (if you can call it that).

Found it while spring cleaning in a folder called "Spaghet Code". Says it all really lol.

This is the latest surviving version. Putting it here as an archeological relic.

## What's in here

- A spaceship, aliens and a mothership. You know the drill.
- Sprite animations, collision detection and waves of enemies.
- Menus, a game-over screen, music and sound effects.
- Me learning how game loops, threads and Java fit together by having a go.

It's Java with AWT/Java2D, without a game engine. Expect some questionable decisions. I was learning.
