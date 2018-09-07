package todd.spike;

import java.util.ArrayList;
import java.util.List;

public class ConciseStackTrace {

    public List<String> logStackTrace(Throwable throwable) {
        List<String> stackTraceDescr = new ArrayList<>();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String fileName = element.getFileName();
            int lineNumber = element.getLineNumber();
            String methodName = element.getMethodName();
            stackTraceDescr.add(fileName + ":" + className + ":" + methodName + ":" + lineNumber);
        }
        return stackTraceDescr;
    }
}
