package todd.spike;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Plugin(name = "ToddCustomJsonLayout", category = "Core", elementType = "layout", printObject = true)
public class ToddCustomJsonLayout extends AbstractCustomJsonLayout {

    private final String environment;

    public ToddCustomJsonLayout(String environment, boolean pretty) {
        super(pretty);
        this.environment = environment;
    }

    @Override
    protected Map<String, Object> mapLogEvent(LogEvent event) {
        Map<String, Object> kvMap = new HashMap<>();
        kvMap.put("threadId", event.getThreadId());
        kvMap.put("instance", event.getThreadName());
        kvMap.put("level", event.getLevel().toString());
        kvMap.put("message", event.getMessage().getFormattedMessage());
        kvMap.put("logger", event.getLoggerName());
        kvMap.put("environment", environment);
        return kvMap;
    }

    @Override
    protected Map<String, Object> logStackTrace(Throwable throwable) {
        Map<String, Object> kvMap = new HashMap<>();
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
        return kvMap;
    }

    @Override
    protected Map<String, Object> logException(Throwable throwable) {
        Map<String, Object> kvMap = new HashMap<>();
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
        return kvMap;
    }


    @PluginFactory
    public static ToddCustomJsonLayout createLayout(
            @PluginAttribute(value = "environment", defaultString = "unknown") String environment,
            @PluginAttribute(value = "pretty", defaultBoolean = false) boolean pretty
    ) {
        return new ToddCustomJsonLayout(environment, pretty);
    }
}
