package todd.spike;

import org.apache.logging.log4j.core.LogEvent;

import java.util.HashMap;
import java.util.Map;

class ConcreteCustomJsonLayoutForTesting extends AbstractCustomJsonLayout {

    public ConcreteCustomJsonLayoutForTesting() {
        super(false);
    }

    public ConcreteCustomJsonLayoutForTesting(boolean pretty) {
        super(pretty);
    }

    @Override
    protected Map<String, Object> mapLogEvent(LogEvent event) {
        HashMap<String, Object> kvMap = new HashMap<>();
        kvMap.put("additionalKey", "additionalValue");
        return kvMap;
    }

    @Override
    protected Map<String, Object> logException(Throwable throwable) {
        HashMap<String, Object> kvMap = new HashMap<>();
        kvMap.put("exception", "dummy exception");
        return kvMap;
    }

    @Override
    protected Map<String, Object> logStackTrace(Throwable throwable) {
        HashMap<String, Object> kvMap = new HashMap<>();
        kvMap.put("stackTrace", "dummy stack trace");
        return kvMap;
    }
}
