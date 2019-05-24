package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.DalaranModelSchema;
import io.terminus.dalaran.model.ModelField;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class XMLSchema implements DalaranModelSchema {

    private String root;

    private List<String> expandableProperties;

    private String elementName;

    private String arrayName;

    private boolean forceTopLevelObject;

    private Map<String, ModelField> fields;
}
