package todd.spike;

import org.apache.logging.log4j.core.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToddCustomJsonLayout extends AbstractCustomJsonLayout {

    private final String environment;

    public ToddCustomJsonLayout(String environment, boolean pretty) {
        super(pretty);
        this.environment = environment;
    }

    @Override
    protected Map<String, Object> addCustomPairsToKvMap(LogEvent event) {
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
}
