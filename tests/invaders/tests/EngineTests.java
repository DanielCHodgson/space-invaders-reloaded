package invaders.tests;

import static invaders.tests.TestSupport.*;

import invaders.app.GameAssembly;
import invaders.asset.*;
import invaders.control.*;
import invaders.encounter.*;
import invaders.entity.Entity;
import invaders.event.GameEvent;
import invaders.input.InputHandler;
import invaders.loop.FixedStepClock;
import invaders.render.*;
import invaders.scene.*;
import invaders.scene.menu.MenuLayout;
import invaders.ui.GamePanel;

import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class EngineTests {
    private EngineTests() {}

    public static void run(Path previews) throws Exception {
        test(
                "fixed-step clock retains fractions and caps catch-up",
                () -> {
                    FixedStepClock clock = new FixedStepClock(60, 5);
                    check(
                            clock.advance(8_000_000) == 0 && clock.advance(9_000_000) == 1,
                            "Fraction retained");
                    check(
                            clock.advance(1_000_000_000) == 5 && clock.advance(-10) == 0,
                            "Catch-up bounded");
                    clock.reset();
                    check(clock.advance(0) == 0, "Reset");
                });
        test(
                "scene lifecycle and overlay history preserve underlying state",
                () -> {
                    int[] entered = {0}, exited = {0}, updates = {0};
                    SceneManager scenes = new SceneManager(() -> {});
                    scenes.register(
                            SceneId.MENU,
                            new Scene() {
                                public void onEnter() {
                                    entered[0]++;
                                }

                                public void onExit() {
                                    exited[0]++;
                                }

                                public void update() {
                                    updates[0]++;
                                }
                            });
                    scenes.register(SceneId.HELP, new Scene() {});
                    scenes.show(SceneId.MENU);
                    scenes.update();
                    scenes.push(SceneId.HELP);
                    scenes.update();
                    scenes.back();
                    check(
                            scenes.currentId() == SceneId.MENU
                                    && entered[0] == 2
                                    && exited[0] == 1
                                    && updates[0] == 1,
                            "Scene lifecycle");
                    try {
                        scenes.show(SceneId.GAME_OVER);
                        throw new AssertionError("Unregistered scene allowed");
                    } catch (IllegalArgumentException expected) {
                        check(
                                scenes.currentId() == SceneId.MENU,
                                "Failed transition leaves active scene intact");
                    }
                });
        test(
                "encounter scene returns victory once to a pluggable outer controller",
                () -> {
                    List<EncounterResult> received = new ArrayList<>();
                    Encounter fake =
                            new Encounter() {
                                public void start() {}

                                public void update() {}

                                public boolean isFinished() {
                                    return true;
                                }

                                public Optional<EncounterResult> result() {
                                    return Optional.of(
                                            new EncounterResult(EncounterOutcome.VICTORY, 123));
                                }

                                public List<GameEvent> drainEvents() {
                                    return List.of();
                                }

                                public void press(GameKey key) {}

                                public void release(GameKey key) {}

                                public void clearInput() {}
                            };
                    EncounterScene scene = new EncounterScene(fake, received::add);
                    scene.onEnter();
                    scene.update();
                    scene.update();
                    check(
                            received.size() == 1
                                    && received.get(0).outcome() == EncounterOutcome.VICTORY,
                            "Outer flow receives result once");
                });
        test(
                "menus, retry and help use shared button geometry",
                () -> {
                    GameFixture f = GameFixture.started();
                    int[] quits = {0};
                    SceneManager scenes =
                            GameAssembly.scenes(f.game(), new MenuLayout(), () -> quits[0]++);
                    scenes.click(300, 250);
                    check(scenes.currentId() == SceneId.HELP, "Help opens");
                    scenes.click(300, 325);
                    check(scenes.currentId() == SceneId.MENU, "Back to origin");
                    scenes.click(300, 175);
                    check(scenes.currentId() == SceneId.PLAYING, "Play starts encounter");
                    f.scenario(new invaders.entity.Alien(100, 350, 1, 0));
                    scenes.update();
                    check(scenes.currentId() == SceneId.GAME_OVER, "Defeat route");
                    scenes.click(300, 250);
                    scenes.click(300, 325);
                    check(scenes.currentId() == SceneId.GAME_OVER, "Help preserves loss");
                    scenes.click(300, 175);
                    check(
                            scenes.currentId() == SceneId.PLAYING
                                    && f.world().enemies().size() == 27,
                            "Retry");
                    scenes.show(SceneId.MENU);
                    scenes.click(300, 325);
                    check(quits[0] == 1, "Quit callback");
                });
        test(
                "viewport maps scaled input and rejects letterboxes",
                () -> {
                    Viewport viewport = Viewport.fit(1200, 1000);
                    var point = viewport.toGame(600, 400).orElseThrow();
                    check(point.x == 300 && point.y == 175, "Scaled hitbox");
                    check(viewport.toGame(10, 10).isEmpty(), "Letterbox ignored");
                });
        test(
                "entity renderer registry accepts a new drawing strategy",
                () -> {
                    EntityRendererRegistry registry = new EntityRendererRegistry();
                    int[] drawn = {0};
                    registry.register(CustomEntity.class, (g, e, t) -> drawn[0]++);
                    BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = image.createGraphics();
                    registry.render(graphics, new CustomEntity(1, 2), 0);
                    graphics.dispose();
                    check(drawn[0] == 1, "Registered renderer invoked");
                });
        SwingUtilities.invokeAndWait(
                () -> {
                    try {
                        test(
                                "physical key aliases, focus loss and pointer scaling are isolated"
                                        + " in adapter",
                                EngineTests::input);
                        test(
                                "all scene renderers paint original assets through the panel",
                                () -> paintScenes(previews));
                    } catch (Exception e) {
                        throw new AssertionError(e);
                    }
                });
    }

    private static void input() {
        GameFixture f = GameFixture.started();
        JPanel panel = new JPanel();
        panel.setSize(600, 450);
        List<java.awt.Point> clicks = new ArrayList<>();
        InputHandler handler =
                new InputHandler(f.game(), (x, y) -> clicks.add(new java.awt.Point(x, y)), panel);
        handler.attach();
        handler.keyPressed(key(panel, KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        handler.keyPressed(key(panel, KeyEvent.KEY_PRESSED, KeyEvent.VK_A));
        handler.keyReleased(key(panel, KeyEvent.KEY_RELEASED, KeyEvent.VK_A));
        f.game().update();
        check(f.world().player().x() == 33, "Releasing alias keeps other key held");
        handler.focusLost(new FocusEvent(panel, FocusEvent.FOCUS_LOST));
        f.game().update();
        check(f.world().player().x() == 33, "Focus clears movement");
        handler.keyPressed(key(panel, KeyEvent.KEY_PRESSED, KeyEvent.VK_SPACE));
        handler.keyPressed(key(panel, KeyEvent.KEY_PRESSED, KeyEvent.VK_SPACE));
        check(f.world().lasers().size() == 1, "OS repeat ignored");
        panel.setSize(1200, 1000);
        handler.mousePressed(
                new MouseEvent(
                        panel,
                        MouseEvent.MOUSE_PRESSED,
                        0,
                        0,
                        600,
                        400,
                        1,
                        false,
                        MouseEvent.BUTTON1));
        check(clicks.equals(List.of(new java.awt.Point(300, 175))), "Pointer mapping");
        handler.detach();
        check(
                panel.getKeyListeners().length == 0 && panel.getMouseListeners().length == 0,
                "Listener lifecycle");
    }

    private static KeyEvent key(JPanel panel, int id, int code) {
        return new KeyEvent(panel, id, 0, 0, code, KeyEvent.CHAR_UNDEFINED);
    }

    private static void paintScenes(Path previews) throws Exception {
        ResourceLoader resources = new ClasspathResourceLoader();
        SpriteSource sprites = new SpriteLibrary(resources, AssetCatalog.load(resources));
        GameFixture f = GameFixture.started();
        MenuLayout menus = new MenuLayout();
        SceneManager scenes = GameAssembly.scenes(f.game(), menus, () -> {});
        GamePanel panel = new GamePanel(GameAssembly.renderer(sprites, f.game(), scenes, menus));
        panel.setSize(600, 450);
        for (SceneId id : SceneId.values()) {
            scenes.show(id);
            BufferedImage image = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            panel.paint(graphics);
            graphics.dispose();
            check(image.getRGB(0, 0) != 0, "Opaque frame");
            if (previews != null) {
                Files.createDirectories(previews);
                ImageIO.write(
                        image,
                        "png",
                        previews.resolve(id.name().toLowerCase(Locale.ROOT) + ".png").toFile());
            }
        }
    }

    private record CustomEntity(int x, int y) implements Entity {
        public int width() {
            return 10;
        }

        public int height() {
            return 10;
        }
    }
}
