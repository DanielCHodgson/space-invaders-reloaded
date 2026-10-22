# Bug fixes and refactor

The original gameplay and assets are retained: a 600 x 450 playfield, three nine-alien rows moving in opposing directions, staggered row respawns, A/D or arrow movement, one shot per Space press, one point per alien and twenty per three-hit mothership. The existing Help button now works.

## Fixed

- One owner for simulation state; input, fixed-step updates and painting run on Swing's event thread.
- Lasers use their actual 8 x 16 bounds, resolve the nearest hit once and are consumed immediately. The mothership uses its full 64-pixel width and takes three separate hits.
- Dead enemies and offscreen projectiles are removed without skipped updates or retained render entries.
- Each row reverses once when its leading edge reaches a boundary. Simultaneous turns cause one formation descent per tick. The whole mothership stays inside the window.
- Row respawns inspect living enemies rather than stale cached references. Boss milestones are remembered, and crossing a milestone still works when a twenty-point award skips the exact number.
- Retry resets the existing session, including score, input and queued sound events. It does not reopen the window or run a second game loop from a mouse callback.
- Held movement keys are tracked independently; releasing one alias does not release another. Losing focus clears input.
- Backgrounds are painted before entities. Swing owns buffering; copied graphics contexts are disposed. The score is drawn once.
- A paced timer replaces the busy loop. Simulation catches up by at most five fixed steps per frame after a stall.
- Textures are loaded and sliced once. Images and audio resolve from the classpath in a packaged JAR; source-tree fallback supports IntelliJ.
- At most four audio clips are cached and reused. Streams close after decoding, clips close on exit, and missing audio fails gracefully.
- The window is created and disposed on the event thread, with a correctly sized content area.

## Structure

- `entity`, `world` and `system`: independent simulation data and gameplay rules.
- `game.GameSession`: orchestrates the Invaders run through the `Encounter` contract.
- `scene`: mode/menu transitions and encounter results returned to an outer controller.
- `render`: registered screen/entity renderers consuming read-only views.
- `asset` and `audio`: metadata, resource loading and device lifecycle behind interfaces.
- `input`, `loop` and `ui`: platform adapters; `app` wires them together.

See [ARCHITECTURE.md](ARCHITECTURE.md) for dependency rules and extension points. Sprite rectangles and animation timing live in `res/assets.properties`; source-image resolution is independent of collision dimensions. The current gameplay retains the original endless/staggered spawning. Finite-wave objectives, exploration and campaign persistence are later work.

The redundant controller/render registries, duplicate interfaces, unused sprite helpers, per-entity sound loading and commented-out implementations have been removed. Original source remains in Git history and in the Class of '18 repository.

## Validation

`build.ps1` compiles for Java 21 with `-Xlint:all -Werror`, checks package dependency boundaries, runs 34 dependency-free regression checks, and packages a runnable JAR including all original assets. Checks cover collision ordering, projectile removal, boss damage and milestones, boundaries, input aliases, focus loss, retries, respawns, immutable views, audio failure/caching/shutdown, and painting/input on the Swing event thread. A deterministic run exercises 30,000 simulation steps across repeated games. Extension checks exercise a custom enemy, renderer, replacement artwork and encounter returning victory.

## Credit

Original game by Daniel Hodgson.
