package todd.spike;

import org.apache.logging.log4j.core.LogEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

}
