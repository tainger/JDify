package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.annotation.Model;
import io.terminus.dalaran.model.annotation.ModelFieldInfo;
import lombok.Data;

import java.util.List;

@Data
@Model(value = "XML")
public class XMLSchema extends DalaranModelSchema {

    @ModelFieldInfo(label = "名称", inputType = FieldInputType.String)
    private String name;

    @ModelFieldInfo(label = "类型", inputType = FieldInputType.ModelSelector)
    private String modelType;

    private String root;

    private List<String> expandableProperties;

    private String elementName;

    private String arrayName;

    private boolean forceTopLevelObject;

}
