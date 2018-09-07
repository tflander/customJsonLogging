package todd.spike;

import java.util.HashMap;
import java.util.Map;

public class DefaultExceptionLogger {

    public Map<String, Object> logException(Throwable throwable) {
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

}
