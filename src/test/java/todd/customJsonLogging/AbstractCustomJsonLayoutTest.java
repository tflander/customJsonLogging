package todd.customJsonLogging;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.junit.Before;
import org.junit.Test;
import todd.customJsonLogging.support.ConcreteCustomJsonLayoutForTesting;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static todd.customJsonLogging.support.JsonValidationUtils.*;

public class AbstractCustomJsonLayoutTest {

    private final ConcreteCustomJsonLayoutForTesting prettyJsonLayout = new ConcreteCustomJsonLayoutForTesting(true);
    private final ConcreteCustomJsonLayoutForTesting conciseJsonLayout = new ConcreteCustomJsonLayoutForTesting(false);

    private MutableLogEvent event;

    @Before
    public void setUp() {
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
        logsKeyAndStringValueForLayout(key, expectedValue, prettyJsonLayout, event);
        logsKeyAndStringValueForLayout(key, expectedValue, conciseJsonLayout, event);
    }

}

