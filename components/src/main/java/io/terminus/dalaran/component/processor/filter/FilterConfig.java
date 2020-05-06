package io.terminus.dalaran.component.processor.filter;


import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

import java.util.List;

@Data
public class FilterConfig {

    @ConfigFieldInfo(label = "过滤条件")
    private List<String> filters;

}
