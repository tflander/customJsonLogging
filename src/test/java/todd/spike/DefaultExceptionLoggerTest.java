package todd.spike;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class DefaultExceptionLoggerTest {

    private final DefaultExceptionLogger exceptionLogger = new DefaultExceptionLogger();

    @Test
    public void logsException() {
        Map<String, Object> exceptions = exceptionLogger.logException(new Exception("whoops"));
        Assertions.assertThat(exceptions).containsExactly(
                getEntry(
                        "exception.0.thrown",
                        "java.lang.Exception:whoops DefaultExceptionLoggerTest.java todd.spike.DefaultExceptionLoggerTest:logsException line 16"
                ));
    }

    @Test
    public void logsFirstCause() {
        Map<String, Object> exceptions = exceptionLogger.logException(new Exception("whoops", new Exception("First Cause")));

        Assertions.assertThat(exceptions).contains(
                getEntry(
                        "exception.0.thrown",
                        "java.lang.Exception:whoops DefaultExceptionLoggerTest.java todd.spike.DefaultExceptionLoggerTest:logsFirstCause line 26"
                ),
                getEntry(
                        "exception.1.cause",
                        "java.lang.Exception:First Cause DefaultExceptionLoggerTest.java todd.spike.DefaultExceptionLoggerTest:logsFirstCause line 26"
                )
        );
    }

    @Test
    public void logsSecondCause() throws IOException {
        Map<String, Object> exceptions = exceptionLogger.logException(
                new Exception("whoops",
                        new Exception("First Cause",
                                new Exception("Second Cause"))));

        Assertions.assertThat(exceptions).contains(
                getEntry(
                        "exception.0.thrown",
                        "java.lang.Exception:whoops DefaultExceptionLoggerTest.java todd.spike.DefaultExceptionLoggerTest:logsSecondCause line 42"
                ),
                getEntry(
                        "exception.1.cause",
                        "java.lang.Exception:First Cause DefaultExceptionLoggerTest.java todd.spike.DefaultExceptionLoggerTest:logsSecondCause line 42"
                ),
                getEntry(
                        "exception.2.cause",
                        "java.lang.Exception:Second Cause DefaultExceptionLoggerTest.java todd.spike.DefaultExceptionLoggerTest:logsSecondCause line 42"
                )
        );

    }

    private Map.Entry<? extends String, ?> getEntry(String key, String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

}