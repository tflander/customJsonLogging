package todd.spike;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Field;

public class SlfSpike {

    Logger log = LoggerFactory.getLogger(SlfSpike.class);
    RequestMetadata metadata = RequestMetadata.builder()
            .action("action")
            .applicationId("appId")
            .eventType("event")
            .stepName("step")
            .traceId("traceId1234")
            .vin("vin1234")
            .build();

    @Before
    public void setUp() throws Exception {
        addRequestMetadataToMdc();
    }

    @Test
    public void simpleWithCustomMdc() {
        MDC.put("firstName", "Dorothy");
        log.info("foo");
    }

    @Test
    public void stacktrace() {
        try {
            FakeService.doIt();
        } catch (Exception e) {
            log.info("failed", e);
        }
    }

    private void addRequestMetadataToMdc() {
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
