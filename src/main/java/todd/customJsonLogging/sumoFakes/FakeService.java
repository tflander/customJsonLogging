package todd.customJsonLogging.sumoFakes;

public class FakeService {
    public static void doIt() {
        try {
            doitImpl();
        } catch (Exception e) {
            throw new IllegalStateException("Whoops", e);
        }
    }

    private static void doitImpl() {
        try {
            doTheThing();
        } catch (Exception e) {
            throw new RuntimeException("rethrow", e);
        }
    }

    private static void doTheThing() throws Exception {
        throw new Exception("error here");
    }
}
