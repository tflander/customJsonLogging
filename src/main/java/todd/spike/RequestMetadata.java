package todd.spike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestMetadata {
    private String traceId;
    private String applicationId;
    private String vin;
    private String eventType;
    private String action;
    private String stepName;
}
