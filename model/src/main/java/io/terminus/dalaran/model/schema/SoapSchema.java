package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.annotation.Model;
import io.terminus.dalaran.model.annotation.ModelFieldInfo;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/6/6
 */
@Data
@Model(value = "SOAP")
public class SoapSchema extends DalaranModelSchema {

    @ModelFieldInfo(label = "命名空间", inputType = FieldInputType.String, defaultValue = "http://schemas.xmlsoap.org/wsdl")
    private String targetNamespace = "http://schemas.xmlsoap.org/wsdl";

    @ModelFieldInfo(label = "命名空间前缀", inputType = FieldInputType.String, defaultValue = "dalaran")
    private String prefix = "dalaran";

    @ModelFieldInfo(label = "根节点包含命名空间", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean bodyContainsXmlns = false;

    @ModelFieldInfo(label = "根节点包含命名前缀", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean bodyContainsPrefix = false;

    @ModelFieldInfo(label = "所有节点包含命名空间", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean allContainsPrefix = false;

    @ModelFieldInfo(label = "移除空节点", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean removeNullColumn = false;

    private MessageModel header;

    private Map<String, Object> headerValues;

    private SoapSchemaOperation operationConfig;
}
