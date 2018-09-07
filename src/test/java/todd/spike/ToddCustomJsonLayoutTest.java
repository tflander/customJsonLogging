package todd.spike;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ToddCustomJsonLayoutTest {

    private static final String TEST_ENVIRONMENT = "Test Environment";
    private static final String LOGGER_NAME = "Test logger";
    public static final int THREAD_ID = 1234;
    private static final String THREAD_NAME = "Thread Name";
    private static final String MESSAGE = "Message";

    // TODO: factory method
    private ToddCustomJsonLayout prettyJsonLayout = new ToddCustomJsonLayout(TEST_ENVIRONMENT, true);
    private ToddCustomJsonLayout conciseJsonLayout = new ToddCustomJsonLayout(TEST_ENVIRONMENT, false);

    private MutableLogEvent event;

    @Before
    public void setUp() throws Exception {
        event = new MutableLogEvent();
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
    public void logsMdcFields() throws IOException {
        StringMap mdcMap = new SortedArrayStringMap();
        mdcMap.putValue("foo", "bar");
        event.setContextData(mdcMap);
        logsKeyAndStringValue("foo", "bar");
    }

    @Test
    public void logsStackTrace() throws IOException {
        event.setThrown(new Exception("Whoops"));
        String logString = conciseJsonLayout.toSerializable(event);
        JsonNode keyNode = getKeyNode(logString, "stackTrace");

        int expectedStackSize = 32;
        assertThat(keyNode.size()).isEqualTo(expectedStackSize);
        assertThat(keyNode.elements().next().asText()).isEqualTo("ToddCustomJsonLayoutTest.java:todd.spike.ToddCustomJsonLayoutTest:logsStackTrace:68");
    }

    @Test
    public void logsException() throws IOException {
        event.setThrown(new Exception("Whoops"));
        logsKeyAndStringValue("exception.0.thrown", "java.lang.Exception:Whoops ToddCustomJsonLayoutTest.java todd.spike.ToddCustomJsonLayoutTest:logsException line 79");
    }

    @Test
    public void logsFirstCause() throws IOException {
        event.setThrown(new Exception("Whoops", new Exception("First Cause")));
        logsKeyAndStringValue("exception.1.cause", "java.lang.Exception:First Cause ToddCustomJsonLayoutTest.java todd.spike.ToddCustomJsonLayoutTest:logsFirstCause line 85");
    }

    @Test
    public void logsSecondCause() throws IOException {
        event.setThrown(new Exception("Whoops", new Exception("First Cause", new Exception("Second Cause"))));
        logsKeyAndStringValue("exception.2.cause", "java.lang.Exception:Second Cause ToddCustomJsonLayoutTest.java todd.spike.ToddCustomJsonLayoutTest:logsSecondCause line 91");
    }

    @Test
    public void prettyFormattingIsMultipleLinesSuitableForHumans() {
        String logString = prettyJsonLayout.toSerializable(event);
        int lineCount = logString.split(System.lineSeparator()).length;
        assertThat(lineCount).isGreaterThan(1);
    }

    @Test
    public void conciseFormattingIsASingleLineSuitableForMachines() {
        String logString = conciseJsonLayout.toSerializable(event);
        int lineCount = logString.split(System.lineSeparator()).length;
        assertThat(lineCount).isEqualTo(1);

    }

    @Test
    public void conciseLogsEndInALineSeparator() {
        String logEntry = conciseJsonLayout.toSerializable(event);
        assertThat(logEntry).endsWith(System.lineSeparator());
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