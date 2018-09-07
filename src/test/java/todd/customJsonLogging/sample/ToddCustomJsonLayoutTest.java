package todd.customJsonLogging.sample;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import todd.customJsonLogging.ConciseStackTraceLogger;
import todd.customJsonLogging.DefaultExceptionLogger;
import todd.customJsonLogging.demo.LoggingDemo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static todd.customJsonLogging.support.JsonValidationUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class ToddCustomJsonLayoutTest {

    private static final String TEST_ENVIRONMENT = "Test Environment";
    private static final String LOGGER_NAME = "Test logger";
    private static final int THREAD_ID = 1234;
    private static final String THREAD_NAME = "Thread Name";
    private static final String MESSAGE = "Message";

    private final LoggingDemo.ToddCustomJsonLayout prettyJsonLayout = LoggingDemo.ToddCustomJsonLayout.createLayout(TEST_ENVIRONMENT, true);
    private final LoggingDemo.ToddCustomJsonLayout conciseJsonLayout = LoggingDemo.ToddCustomJsonLayout.createLayout(TEST_ENVIRONMENT, false);

    private MutableLogEvent event;

    @Mock
    private ConciseStackTraceLogger conciseStackTraceLogger;

    @Mock
    private DefaultExceptionLogger defaultExceptionLogger;

    @Before
    public void setUp() {
        event = new MutableLogEvent();
        prettyJsonLayout.setConciseStackTraceForTesting(conciseStackTraceLogger);
        prettyJsonLayout.setExceptionLoggerForTesting(defaultExceptionLogger);
        conciseJsonLayout.setConciseStackTraceForTesting(conciseStackTraceLogger);
        conciseJsonLayout.setExceptionLoggerForTesting(defaultExceptionLogger);
    }

    @Test
    public void logsEnvironment() throws IOException {
        logsKeyAndStringValue("environment", TEST_ENVIRONMENT);
    }

    @Test
    public void logsEventFields() throws IOException {
        event.setLoggerName(LOGGER_NAME);
        event.setLevel(Level.FATAL);
        event.setThreadId(THREAD_ID);
        event.setThreadName(THREAD_NAME);
        event.setMessage(new SimpleMessage(MESSAGE));

        logsKeyAndStringValue("logger", LOGGER_NAME);
        logsKeyAndStringValue("level", Level.FATAL.toString());
        logsKeyAndLongValue("threadId", THREAD_ID);
        logsKeyAndStringValue("instance", THREAD_NAME);
        logsKeyAndStringValue("message", MESSAGE);
    }

    @Test
    public void logsStackTrace() throws IOException {

        Exception exception = new Exception("Whoops");
        event.setThrown(exception);
        List<String> stackTrace = Arrays.asList("mock trace line one", "mock trace line two");
        when(conciseStackTraceLogger.logStackTrace(exception)).thenReturn(stackTrace);
        String logString = conciseJsonLayout.toSerializable(event);
        JsonNode keyNode = getKeyNode(logString, "stackTrace");

        assertThat(keyNode.size()).isEqualTo(stackTrace.size());
        assertThat(keyNode.elements().next().asText()).isEqualTo(stackTrace.get(0));
    }

    @Test
    public void logsException() throws IOException {
        Exception exception = new Exception("Whoops");
        event.setThrown(exception);
        Map<String, Object> exceptions = new HashMap<>();
        exceptions.put("exception key", "exception description");
        when(defaultExceptionLogger.logException(exception)).thenReturn(exceptions);
        logsKeyAndStringValue("exception key", "exception description");
    }

    private void logsKeyAndStringValue(String key, String expectedValue) throws IOException {
        logsKeyAndStringValueForLayout(key, expectedValue, prettyJsonLayout, event);
        logsKeyAndStringValueForLayout(key, expectedValue, conciseJsonLayout, event);
    }

    private void logsKeyAndLongValue(String key, long expectedValue) throws IOException {
        logsKeyAndLongValueForLayout(key, expectedValue, prettyJsonLayout, event);
        logsKeyAndLongValueForLayout(key, expectedValue, conciseJsonLayout, event);
    }

}