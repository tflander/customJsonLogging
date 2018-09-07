package todd.spike;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCustomJsonLayoutExceptionTest {

    @InjectMocks
    ConcreteCustomJsonLayoutForTesting jsonLayout;

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

