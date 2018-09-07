package todd.customJsonLogging;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConciseStackTraceLoggerTest {

    @Test
    public void logStackTrace() {

        ConciseStackTraceLogger conciseStackTraceLogger = new ConciseStackTraceLogger();
        List<String> stackTrace = conciseStackTraceLogger.logStackTrace(new Exception("whoops"));

        int expectedStackSize = 31;
        assertThat(stackTrace.size()).isEqualTo(expectedStackSize);
        assertThat(stackTrace.get(0)).isEqualTo("ConciseStackTraceLoggerTest.java:todd.customJsonLogging.ConciseStackTraceLoggerTest:logStackTrace:15");
    }
}

