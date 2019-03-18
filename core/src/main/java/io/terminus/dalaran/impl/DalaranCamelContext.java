package io.terminus.dalaran.impl;

import com.google.gson.Gson;
import io.terminus.dalaran.Component;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.model.DalaranComponentInstance;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.util.DalaranPropertyUtils;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DalaranCamelContext implements DalaranContext {

    private CamelContext camelContext = new DefaultCamelContext();
    private final Map<String, DalaranComponentContainer<DalaranTrigger>> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranComponentContainer<DalaranProcessor>> processorMapping = new ConcurrentHashMap<>();

    // TODO Flow 可以抽象一个 Builder, 但暂时没有必要
    @Override
    public void addFlow(DalaranFlow dalaranFlow) {
        val route = new RouteDefinition();
        // TODO route need an u id
//        route.setId(UID);
        val trigger = dalaranFlow.getTrigger();
        val processorList = dalaranFlow.getProcessors();
        val triggerContainer = getTriggerContainer(trigger.getType());
        // TODO 替换 properties
        // TODO check

        // TODO 临时扔一下, 这部分要抽出去, config 也需要 cache
        Object config = DalaranPropertyUtils.convertConfig(trigger.getConfig(), dalaranFlow.getProperties(), triggerContainer.getConfigClass());
        route.from(triggerContainer.getComponent().buildRouterUri(config));
        for (DalaranComponentInstance processor : processorList) {
            val processorContainer = getProcessorContainer(processor.getType());
            Object processorConfig = DalaranPropertyUtils.convertConfig(processor.getConfig(), dalaranFlow.getProperties(), processorContainer.getConfigClass());
            processorContainer.getComponent().configure(route, processorConfig);
        }
        try {
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFlows(List<DalaranFlow> flows) {
        flows.forEach(this::addFlow);
    }

    @Override
    public void addTrigger(String triggerType, Class configType, DalaranTrigger trigger) {
        val componentContainer = new DalaranComponentContainer<DalaranTrigger>(triggerType, configType, trigger);
        triggerMapping.put(triggerType, componentContainer);
    }

    @Override
    public void addProcessor(String processorType, Class configType, DalaranProcessor processor) {
        val componentContainer = new DalaranComponentContainer<DalaranProcessor>(processorType, configType, processor);
        processorMapping.put(processorType, componentContainer);
    }

    @Override
    public DalaranComponentContainer<DalaranTrigger> getTriggerContainer(String type) {
        return triggerMapping.get(type);
    }

    @Override
    public DalaranComponentContainer<DalaranProcessor> getProcessorContainer(String type) {
        return processorMapping.get(type);
    }

    @PostConstruct
    public void loadComponents() {
        // TODO 可以用 spring 的 annotation, 可以少些一个 service load file
        ServiceLoader.load(Component.class).forEach(component -> {
            Class componentClass = component.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent == null) {
                return;
            }
            String componentType = dalaranComponent.value();
            if (component instanceof DalaranTrigger) {
                addTrigger(componentType, dalaranComponent.configType(), (DalaranTrigger) component);
            } else if (component instanceof DalaranProcessor) {
                addProcessor(componentType, dalaranComponent.configType(), (DalaranProcessor) component);
            }
        });
    }

    @Override
    public void loadFlows() {
        Gson gson = new Gson();
        try {
            camelContext.start();
            URL in = DalaranCamelContext.class.getResource("/dalaran");
            File file = new File(in.toURI());
            for (File messageFlowFile : file.listFiles()) {
                Reader reader = new InputStreamReader(new FileInputStream(messageFlowFile));
                DalaranFlow dalaranFlow = gson.fromJson(reader, DalaranFlow.class);
                addFlow(dalaranFlow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
