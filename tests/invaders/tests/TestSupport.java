package invaders.tests;

public final class TestSupport {
    private static int passed;

    private TestSupport() {}

    @FunctionalInterface
    public interface Check {
        void run() throws Exception;
    }

    public static void test(String name, Check action) throws Exception {
        action.run();
        passed++;
        System.out.println("PASS: " + name);
    }

    public static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static int passed() {
        return passed;
    }
}
