package invaders.app;

import invaders.asset.*;
import invaders.encounter.Encounter;
import invaders.entity.*;
import invaders.render.*;
import invaders.scene.*;
import invaders.scene.menu.MenuLayout;
import invaders.world.GameView;

/**
 * Composition root: register scenes and rendering strategies here, rather than inside the model.
 */
public final class GameAssembly {
    private GameAssembly() {}

    public static SceneManager scenes(Encounter game, MenuLayout menus, Runnable quit) {
        SceneManager scenes = new SceneManager(quit);
        scenes.register(SceneId.MENU, new MenuScene(SceneId.MENU, game, scenes, menus));
        scenes.register(SceneId.GAME_OVER, new MenuScene(SceneId.GAME_OVER, game, scenes, menus));
        scenes.register(
                SceneId.PLAYING,
                new EncounterScene(game, result -> scenes.show(SceneId.GAME_OVER)));
        scenes.register(SceneId.HELP, new HelpScene(scenes, menus));
        scenes.show(SceneId.MENU);
        return scenes;
    }

    public static GameRenderer renderer(
            SpriteSource sprites, GameView game, SceneView scenes, MenuLayout menus) {
        EntityRendererRegistry entities = new EntityRendererRegistry();
        entities.register(Player.class, new SpriteEntityRenderer<>(sprites, SpriteId.PLAYER));
        entities.register(Alien.class, new SpriteEntityRenderer<>(sprites, SpriteId.ALIEN));
        entities.register(
                Mothership.class, new SpriteEntityRenderer<>(sprites, SpriteId.MOTHERSHIP));
        entities.register(Laser.class, new SpriteEntityRenderer<>(sprites, SpriteId.LASER));
        MenuPainter painter = new MenuPainter(menus);
        GameRenderer renderer = new GameRenderer(sprites, scenes);
        renderer.register(SceneId.MENU, new MenuRenderer(SceneId.MENU, "SPACE INVADERS", painter));
        renderer.register(
                SceneId.GAME_OVER, new MenuRenderer(SceneId.GAME_OVER, "GAME OVER", painter));
        renderer.register(SceneId.HELP, new HelpRenderer(painter));
        renderer.register(SceneId.PLAYING, new GameplayRenderer(entities, game));
        return renderer;
    }
}
