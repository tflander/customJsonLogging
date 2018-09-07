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

public class AbstractCustomJsonLayoutTest {

    private ConcreteCustomJsonLayoutForTesting prettyJsonLayout = new ConcreteCustomJsonLayoutForTesting(true);
    private ConcreteCustomJsonLayoutForTesting conciseJsonLayout = new ConcreteCustomJsonLayoutForTesting(false);

    private MutableLogEvent event;

    @Before
    public void setUp() throws Exception {
        event = new MutableLogEvent();
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

        assertThat(keyNode.asText()).isEqualTo("dummy stack trace");
    }

    @Test
    public void logsException() throws IOException {
        event.setThrown(new Exception("Whoops"));
        logsKeyAndStringValue("exception", "dummy exception");
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

    private void logsKeyAndStringValueForLayout(String key, String expectedValue, ConcreteCustomJsonLayoutForTesting jsonLayout) throws IOException {
        String jsonString = jsonLayout.toSerializable(event);
        verifyJsonContainsString(jsonString, key, expectedValue);
    }

    private void logsKeyAndLongValueForLayout(String key, long expectedValue, ConcreteCustomJsonLayoutForTesting layout) throws IOException {
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

