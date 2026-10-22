package invaders.tests;

public final class RegressionSuite {
    private RegressionSuite() {}

    public static void main(String[] args) throws Exception {
        GameplayTests.run();
        ResourceTests.run();
        AudioTests.run();
        EngineTests.run(args.length == 0 ? null : java.nio.file.Path.of(args[0]));
        System.out.println("All " + TestSupport.passed() + " regression checks passed.");
    }
}
