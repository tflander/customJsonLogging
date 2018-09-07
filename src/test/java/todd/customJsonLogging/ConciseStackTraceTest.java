package todd.customJsonLogging;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConciseStackTraceTest {

    @Test
    public void logStackTrace() {

        ConciseStackTrace conciseStackTrace = new ConciseStackTrace();
        List<String> stackTrace = conciseStackTrace.logStackTrace(new Exception("whoops"));

        int expectedStackSize = 31;
        assertThat(stackTrace.size()).isEqualTo(expectedStackSize);
        assertThat(stackTrace.get(0)).isEqualTo("ConciseStackTraceTest.java:todd.customJsonLogging.ConciseStackTraceTest:logStackTrace:15");
    }
}

