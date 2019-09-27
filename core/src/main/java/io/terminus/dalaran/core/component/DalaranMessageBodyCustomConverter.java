package io.terminus.dalaran.core.component;

import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.RouteDefinition;

public interface DalaranMessageBodyCustomConverter<T> {

    /**
     * @param route           route 对象
     * @param config          当前节点的配置
     * @param currentBodyType 当前 Body 类型
     * @return 是否需要执行默认初始化
     */
    boolean customBodyConvert(RouteDefinition route, T config, BodyType currentBodyType);
}
