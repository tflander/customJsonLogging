package todd.spike;

import java.util.Map;

public class ToddCustomJsonLayout extends AbstractCustomJsonLayout {

    private final String environment;

    public ToddCustomJsonLayout(String environment, boolean pretty) {
        super(pretty);
        this.environment = environment;
    }

    @Override
    protected void addCustomPairsToKvMap(Map<String, Object> kvMap) {
        kvMap.put("environment", environment);
    }
}
