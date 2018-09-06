package todd.spike;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Plugin(name = "MyCustomJsonLayout", category = "Core", elementType = "layout", printObject = true)
public class MyCustomJsonLayout extends AbstractStringLayout {

    private final String environment;
    private final boolean pretty;
    private ObjectMapper objectMapper;

    private MyCustomJsonLayout(String environment, boolean pretty) {
        super(Charset.forName("UTF-8"));
        this.environment = environment;
        this.pretty = pretty;
        objectMapper = new ObjectMapper();
    }

    private MyCustomJsonLayout() {
        super(Charset.forName("UTF-8"));
        this.environment = "unknown";
        this.pretty = false;
        objectMapper = new ObjectMapper();
    }

    @Override
    public String toSerializable(LogEvent event) {

        Map<String, Object> kvMap = new HashMap<>();
        kvMap.put("threadId", event.getThreadId());
        kvMap.put("instance", event.getThreadName());
        kvMap.put("level", event.getLevel().toString());
        kvMap.put("message", event.getMessage().getFormattedMessage());
        kvMap.put("environment", environment);
        kvMap.put("logger", event.getLoggerName());

        Throwable throwable = event.getThrown();
        if (throwable != null) {
            logStackTrace(kvMap, throwable);
            logExceptionRecursively(kvMap, throwable);
        }

        kvMap.putAll(event.getContextData().toMap());
        try {
            if (!pretty) {
                return objectMapper.writeValueAsString(kvMap); // + System.lineSeparator();
            } else {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(kvMap) + System.lineSeparator();
            }
        } catch (JsonProcessingException e) {
            return e.getClass().getName() + ": " + e.getMessage();
        }
    }

    private void logExceptionRecursively(Map<String, Object> kvMap, Throwable throwable) {
        int level = 0;
        String tag = "exception." + level + ".thrown";
        while (throwable != null) {
            StackTraceElement element = throwable.getStackTrace()[0];
            String exception =
                    throwable.getClass().getName() + ":"
                            + throwable.getMessage() + " "
                            + element.getFileName() + " "
                            + element.getClassName() + ":"
                            + element.getMethodName() + " line "
                            + element.getLineNumber();

            kvMap.put(tag, exception);
            ++level;
            tag = "exception." + level + ".cause";

            if (throwable == throwable.getCause()) {
                throwable = null;
            } else {
                throwable = throwable.getCause();
            }
        }
    }

    private void logStackTrace(Map<String, Object> kvMap, Throwable throwable) {
        List<String> stackTraceDescr = new ArrayList<>();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String fileName = element.getFileName();
            int lineNumber = element.getLineNumber();
            String methodName = element.getMethodName();
            stackTraceDescr.add(fileName + ":" + className + ":" + methodName + ":" + lineNumber);
        }
        kvMap.put("stackTrace", stackTraceDescr);
    }

    @PluginFactory
    public static MyCustomJsonLayout createLayout(
            @PluginAttribute(value = "environment", defaultString = "unknown") String environment,
            @PluginAttribute(value = "pretty", defaultBoolean = false) boolean pretty
    ) {
        return new MyCustomJsonLayout(environment, pretty);
    }
}
