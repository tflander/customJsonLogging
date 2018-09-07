package todd.spike;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToddCustomJsonLayoutTest {

    private static final String TEST_ENVIRONMENT = "Test Environment";
    private static final String LOGGER_NAME = "Test logger";
    public static final int THREAD_ID = 1234;
    private static final String THREAD_NAME = "Thread Name";
    private static final String MESSAGE = "Message";

    private ToddCustomJsonLayout prettyJsonLayout = ToddCustomJsonLayout.createLayout(TEST_ENVIRONMENT, true);
    private ToddCustomJsonLayout conciseJsonLayout = ToddCustomJsonLayout.createLayout(TEST_ENVIRONMENT, false);

    private MutableLogEvent event;

    @Mock
    private ConciseStackTrace conciseStackTrace;

    @Mock
    private DefaultExceptionLogger defaultExceptionLogger;

    @Before
    public void setUp() {
        event = new MutableLogEvent();
        prettyJsonLayout.setConciseStackTraceForTesting(conciseStackTrace);
        prettyJsonLayout.setExceptionLoggerForTesting(defaultExceptionLogger);
        conciseJsonLayout.setConciseStackTraceForTesting(conciseStackTrace);
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
        when(conciseStackTrace.logStackTrace(exception)).thenReturn(stackTrace);
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
        logsKeyAndStringValueForLayout(key, expectedValue, prettyJsonLayout);
        logsKeyAndStringValueForLayout(key, expectedValue, conciseJsonLayout);
    }

    private void logsKeyAndLongValue(String key, long expectedValue) throws IOException {
        logsKeyAndLongValueForLayout(key, expectedValue, prettyJsonLayout);
        logsKeyAndLongValueForLayout(key, expectedValue, conciseJsonLayout);
    }

    private void logsKeyAndStringValueForLayout(String key, String expectedValue, ToddCustomJsonLayout jsonLayout) throws IOException {
        String jsonString = jsonLayout.toSerializable(event);
        verifyJsonContainsString(jsonString, key, expectedValue);
    }

    private void logsKeyAndLongValueForLayout(String key, long expectedValue, ToddCustomJsonLayout layout) throws IOException {
        String jsonString = layout.toSerializable(event);
        verifyJsonContainsLong(jsonString, key, expectedValue);
    }

    private void verifyJsonContainsLong(String logString, String key, long expectedValue) throws IOException {
        JsonNode keyNode = getKeyNode(logString, key);
        assertThat(keyNode.asLong()).isEqualTo(expectedValue);
    }

    private void verifyJsonContainsString(String logString, String key, String expectedValue) throws IOException {
        JsonNode keyNode = getKeyNode(logString, key);
        assertThat(keyNode.asText()).isEqualTo(expectedValue);
    }

    private JsonNode getKeyNode(String logString, String key) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(logString);
        JsonNode keyNode = jsonNode.get(key);
        if (keyNode == null) {
            Assertions.fail("key " + key + " not found in " + logString);
        }
        return keyNode;
    }
}