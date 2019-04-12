package io.terminus.dalaran.component.processor.script;

import io.terminus.dalaran.ModelOptionalConfig;
import lombok.Data;

@Data
public class DalaranScriptConfig extends ModelOptionalConfig {

    private DalaranScriptType type;

    private String script;
}
