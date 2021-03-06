package todd.customJsonLogging.sample;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import todd.customJsonLogging.AbstractCustomJsonLayout;
import todd.customJsonLogging.ConciseStackTraceLogger;
import todd.customJsonLogging.DefaultExceptionLogger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Plugin(name = "ToddCustomJsonLayout", category = "Core", elementType = "layout", printObject = true)
public class ToddCustomJsonLayout extends AbstractCustomJsonLayout {

    private final String environment;
    private ConciseStackTraceLogger conciseStackTraceLogger;
    private DefaultExceptionLogger defaultExceptionLogger;

    public ToddCustomJsonLayout(String environment, boolean pretty) {
        super(pretty);
        this.environment = environment;
        conciseStackTraceLogger = new ConciseStackTraceLogger();
        defaultExceptionLogger = new DefaultExceptionLogger();
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

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimeMillis()), ZoneId.systemDefault());
        kvMap.put("logtimestamp", DateTimeFormatter.ISO_DATE_TIME.format(localDateTime));
        return kvMap;
    }

    @Override
    protected Map<String, Object> logException(Throwable throwable) {
        Map<String, Object> kvMap = new HashMap<>();
        kvMap.put("stackTrace", conciseStackTraceLogger.logStackTrace(throwable));
        kvMap.putAll(defaultExceptionLogger.logException(throwable));
        return kvMap;
    }

    @PluginFactory
    public static ToddCustomJsonLayout createLayout(
            @PluginAttribute(value = "environment", defaultString = "unknown") String environment,
            @PluginAttribute(value = "pretty", defaultBoolean = false) boolean pretty
    ) {
        return new ToddCustomJsonLayout(environment, pretty);
    }

    public void setConciseStackTraceForTesting(ConciseStackTraceLogger conciseStackTraceLogger) {
        this.conciseStackTraceLogger = conciseStackTraceLogger;
    }

    public void setExceptionLoggerForTesting(DefaultExceptionLogger defaultExceptionLogger) {
        this.defaultExceptionLogger = defaultExceptionLogger;
    }
}
