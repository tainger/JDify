package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.DalaranModelSchema;
import lombok.Data;

import java.util.List;

@Data
public class XMLSchema extends DalaranModelSchema {

    private String root;

    private List<String> expandableProperties;

    private String elementName;

    private String arrayName;

    private boolean forceTopLevelObject;

}
