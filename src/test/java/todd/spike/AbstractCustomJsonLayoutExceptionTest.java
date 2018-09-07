package todd.spike;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCustomJsonLayoutExceptionTest {

    @InjectMocks
    ConcreteCustomJsonLayout jsonLayout;

    @Mock
    ObjectMapper objectMapper;

    private MutableLogEvent event = new MutableLogEvent();

    @Test
    public void reportsInvalidJsonRequest() throws JsonProcessingException {
        JsonMappingException exception = InvalidDefinitionException.from((JsonParser) null, null);
        when(objectMapper.writeValueAsString(any(Map.class))).thenThrow(exception);

        assertThat(jsonLayout.toSerializable(event)).isEqualTo("com.fasterxml.jackson.databind.JsonMappingException: N/A");
    }

}

class ConcreteCustomJsonLayout extends AbstractCustomJsonLayout {

    public ConcreteCustomJsonLayout() {
        super(false);
    }

    @Override
    protected Map<String, Object> addCustomPairsToKvMap(LogEvent event) {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Object> logExceptionRecursively(Throwable throwable) {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Object> logStackTrace(Throwable throwable) {
        return new HashMap<>();
    }
}