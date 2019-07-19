package io.terminus.dalaran.core.model.schema;

import io.terminus.dalaran.core.model.DalaranModelSchema;
import io.terminus.dalaran.core.model.ModelField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class XMLSchema extends DalaranModelSchema {

    private String root;

    private List<String> expandableProperties;

    private String elementName;

    private String arrayName;

    private boolean forceTopLevelObject;

}
