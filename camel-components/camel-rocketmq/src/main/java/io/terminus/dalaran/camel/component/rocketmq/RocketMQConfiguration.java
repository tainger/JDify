package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.spi.HeaderFilterStrategyAware;
import org.apache.camel.spi.UriParams;

/**
 * Created by jingdi on 2019/6/18
 */
@UriParams
public class RocketMQConfiguration implements Cloneable, HeaderFilterStrategyAware {

    @Override
    public HeaderFilterStrategy getHeaderFilterStrategy() {
        return null;
    }

    @Override
    public void setHeaderFilterStrategy(HeaderFilterStrategy headerFilterStrategy) {

    }
}
