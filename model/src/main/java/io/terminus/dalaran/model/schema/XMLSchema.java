package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.annotation.Model;
import lombok.Data;

import java.util.List;

@Data
@Model(value = "XML")
public class XMLSchema extends DalaranModelSchema {

    private String root;

    private List<String> expandableProperties;

    private String elementName;

    private String arrayName;

    private boolean forceTopLevelObject;

}
