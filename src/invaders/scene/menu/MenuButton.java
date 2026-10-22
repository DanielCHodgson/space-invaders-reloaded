package invaders.scene.menu;

public record MenuButton(MenuAction action, String label, int x, int y, int width, int height) {
    public boolean contains(int px, int py) {
        return px >= x && px < x + width && py >= y && py < y + height;
    }
}
