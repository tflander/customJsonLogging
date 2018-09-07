package todd.spike;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractCustomJsonLayout extends AbstractStringLayout {

    private final boolean pretty;
    private ObjectMapper objectMapper;

    public AbstractCustomJsonLayout(boolean pretty) {
        super(Charset.forName("UTF-8"));
        this.pretty = pretty;
        objectMapper = new ObjectMapper();
    }

    @Override
    public String toSerializable(LogEvent event) {

        Map<String, Object> kvMap = addCustomPairsToKvMap(event);
        Throwable throwable = event.getThrown();
        if (throwable != null) {
            kvMap.putAll(logStackTrace(throwable));
            kvMap.putAll(logExceptionRecursively(throwable));
        }

        kvMap.putAll(event.getContextData().toMap());
        try {
            if (!pretty) {
                return objectMapper.writeValueAsString(kvMap) + System.lineSeparator();
            } else {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(kvMap) + System.lineSeparator();
            }
        } catch (JsonProcessingException e) {
            return e.getClass().getName() + ": " + e.getMessage();
        }
    }

    protected abstract Map<String, Object> addCustomPairsToKvMap(LogEvent event);
    protected abstract Map<String, Object> logExceptionRecursively(Throwable throwable);
    protected abstract Map<String, Object> logStackTrace(Throwable throwable);

}
