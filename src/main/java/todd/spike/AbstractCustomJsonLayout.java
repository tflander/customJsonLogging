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

    protected AbstractCustomJsonLayout(boolean pretty) {
        super(Charset.forName("UTF-8"));
        this.pretty = pretty;
        objectMapper = new ObjectMapper();
    }

    @Override
    public String toSerializable(LogEvent event) {

        Map<String, Object> kvMap = addCustomPairsToKvMap(event);
        Throwable throwable = event.getThrown();
        if (throwable != null) {
            logStackTrace(kvMap, throwable);
            logExceptionRecursively(kvMap, throwable);
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

}
