package io.terminus.dalaran.component.trigger.soap;

import io.terminus.dalaran.core.component.DalaranTrigger;
import org.apache.camel.model.RouteDefinition;

/**
 * Created by jingdi on 2019/6/13
 */
public class DalaranSoapListener implements DalaranTrigger<SoapListenerConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, SoapListenerConfig config) {

    }
}
