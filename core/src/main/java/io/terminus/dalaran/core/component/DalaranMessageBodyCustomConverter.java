package io.terminus.dalaran.core.component;

import org.apache.camel.model.RouteDefinition;

public interface DalaranMessageBodyCustomConverter<T> {

    /**
     * @param route            route 对象
     * @param config           当前节点的配置
     * @param bodyIsSerialized 当前 Body 是否已经序列化
     * @return 是否需要执行默认初始化
     */
    boolean customBodyConvert(RouteDefinition route, T config, boolean bodyIsSerialized);
}
