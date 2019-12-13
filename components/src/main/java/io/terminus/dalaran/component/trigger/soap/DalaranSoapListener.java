package io.terminus.dalaran.component.trigger.soap;

import io.terminus.dalaran.component.trigger.rest.processor.QueryStringSignProcessor;
import io.terminus.dalaran.component.trigger.soap.model.SoapApiInfo;
import io.terminus.dalaran.component.trigger.soap.model.SoapAuthType;
import io.terminus.dalaran.component.trigger.soap.processor.SoapBasicSignProcessor;
import io.terminus.dalaran.component.trigger.soap.processor.SoapTriggerAfterProcessor;
import io.terminus.dalaran.component.trigger.soap.processor.SoapTriggerProcessor;
import io.terminus.dalaran.component.trigger.soap.utils.WSDLUtils;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerApiDocExport;
import io.terminus.dalaran.core.component.DalaranTriggerBuildAfterProcessor;
import io.terminus.dalaran.core.component.DalaranTriggerWordDocExport;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.context.DalaranClientContext;
import io.terminus.dalaran.model.flow.TriggerFlow;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jingdi on 2019/6/13
 */
@Trigger(
        value = "soap-listener",
        order = 13,
        configType = SoapListenerConfig.class,
        bodyType = "SOAP"
)
public class DalaranSoapListener implements DalaranTrigger<SoapListenerConfig>, DalaranTriggerBuildAfterProcessor<SoapListenerConfig>, DalaranTriggerApiDocExport<String>, DalaranTriggerWordDocExport {

    @Autowired
    private DalaranClientContext clientContext;

    @Override
    public void buildFromRoute(RouteDefinition route, SoapListenerConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri).process(new SoapTriggerProcessor());

        if (!config.isEnableSign()) {
            return;
        }
        if (config.getAuthType() == SoapAuthType.BASIC) {
            route.process(new SoapBasicSignProcessor(clientContext.getAllClient()));
        }
        if (config.getAuthType() == SoapAuthType.CUSTOM) {
            route.process(new QueryStringSignProcessor(clientContext.getAllClient()));
        }
    }

    @Override
    public String exportApiDoc(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        List<SoapApiInfo> apiInfoList = moduleTriggerFlows.entrySet().stream().flatMap(module ->
                module.getValue().stream().map(SoapApiInfo::new)
        ).collect(Collectors.toList());
        return WSDLUtils.buildDefinitions(apiInfoList, null).getAsString();
    }

    @Override
    public File exportWord(Map<String, List<TriggerFlow>> moduleTriggerFlows) {

        return null;
    }

    @Override
    public void buildAfter(RouteDefinition route, SoapListenerConfig config) {
        route.process(new SoapTriggerAfterProcessor(config));
    }
}
