package todd.customJsonLogging.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.LogEvent;
import org.assertj.core.api.Assertions;
import todd.customJsonLogging.AbstractCustomJsonLayout;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public interface JsonValidationUtils {

    static void logsKeyAndStringValueForLayout(String key, String expectedValue, AbstractCustomJsonLayout jsonLayout, LogEvent event) throws IOException {
        String jsonString = jsonLayout.toSerializable(event);
        verifyJsonContainsString(jsonString, key, expectedValue);
    }

    static void verifyJsonContainsString(String logString, String key, String expectedValue) throws IOException {
        JsonNode keyNode = getKeyNode(logString, key);
        assertThat(keyNode.asText()).isEqualTo(expectedValue);
    }

    static void logsKeyAndLongValueForLayout(String key, long expectedValue, AbstractCustomJsonLayout layout, LogEvent event) throws IOException {
        String jsonString = layout.toSerializable(event);
        verifyJsonContainsLong(jsonString, key, expectedValue);
    }

    static void verifyJsonContainsLong(String logString, String key, long expectedValue) throws IOException {
        JsonNode keyNode = getKeyNode(logString, key);
        assertThat(keyNode.asLong()).isEqualTo(expectedValue);
    }


    static JsonNode getKeyNode(String logString, String key) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(logString);
        JsonNode keyNode = jsonNode.get(key);
        if (keyNode == null) {
            Assertions.fail("key " + key + " not found in " + logString);
        }
        return keyNode;
    }

}
