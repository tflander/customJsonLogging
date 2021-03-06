package todd.customJsonLogging.demo;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import todd.customJsonLogging.AbstractCustomJsonLayout;
import todd.customJsonLogging.ConciseStackTraceLogger;
import todd.customJsonLogging.DefaultExceptionLogger;
import todd.customJsonLogging.sumoFakes.FakeService;
import todd.customJsonLogging.sumoFakes.RequestMetadata;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class LoggingDemo {

    static final Logger log = LoggerFactory.getLogger(LoggingDemo.class);
    static final RequestMetadata metadata = RequestMetadata.builder()
            .action("action")
            .applicationId("appId")
            .eventType("event")
            .stepName("step")
            .traceId("traceId1234")
            .vin("vin1234")
            .build();

    public static void main(String[] args) {
        addRequestMetadataToMdc();

        MDC.put("firstName", "Dorothy");
        log.info("foo");

        try {
            FakeService.doIt();
        } catch (Exception e) {
            log.info("failed", e);
        }

    }

    private static void addRequestMetadataToMdc() {
        Field[] fields = RequestMetadata.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                MDC.put(field.getName(), field.get(metadata).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
