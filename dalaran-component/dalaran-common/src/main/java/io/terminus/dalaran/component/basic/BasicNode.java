package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Node")
public class BasicNode implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "节点名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "公司", inputType = FieldInputType.String)
    private String company;

    @ConfigFieldInfo(label = "应用", inputType = FieldInputType.String)
    private String application;

    @ConfigFieldInfo(label = "系统", inputType = FieldInputType.String)
    private String system;

    @ConfigFieldInfo(label = "图标", inputType = FieldInputType.String)
    private String icon;

    @ConfigFieldInfo(label = "图标背景色", inputType = FieldInputType.String)
    private String iconColour;




}
