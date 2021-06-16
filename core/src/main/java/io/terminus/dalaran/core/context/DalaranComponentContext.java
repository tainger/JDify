package io.terminus.dalaran.core.context;

import io.terminus.dalaran.config.*;
import io.terminus.dalaran.core.component.*;

import java.util.Collection;
import java.util.Map;

public interface DalaranComponentContext {

    DalaranTrigger getTrigger(String triggerType);

    DalaranProcessor getProcessor(String group, String processorType, String version);

    TriggerInfo getTriggerInfo(String triggerType);

    ProcessorInfo getProcessorInfo(String group, String processorType, String version);

    boolean removeProcessorInfo(String group, String processorType, String version);

    Collection<TriggerInfo> getAllTriggerInfo();

    Collection<ConnectorInfo> getAllConnectorInfo();

    Collection<LimiterInfo> getAllLimiterInfo();

    Collection<AuthenticatorInfo> getAllAuthenticatorInfo();

    Collection<ProcessorInfo> getAllProcessorInfo();

    void addProcessor(DalaranProcessor bean, String type, String version);

    void addTrigger(DalaranTrigger bean);

    void addBasicComponent(DalaranBasicComponent bean);

    void addAuthenticator(DalaranAuthenticator bean);

    Collection<BasicComponentInfo> getAllBasicComponentInfo();

    Map<String, Map<String, Map<String, ProcessorInfo>>> listAllGroupProcessor();

    Collection<DynamicConfigInfo> getAllDynamicConfigInfo();

    Map<String, Class> getTriggerConfigMap();

    Map<String, Class> getProcessorConfigMap();
}
