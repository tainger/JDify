package io.terminus.dalaran.service.swagger;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class SwaggerImportConfig {

    @ConfigFieldInfo(label = "Swagger URL", inputType = FieldInputType.String)
    private String swaggerUrl;
}
