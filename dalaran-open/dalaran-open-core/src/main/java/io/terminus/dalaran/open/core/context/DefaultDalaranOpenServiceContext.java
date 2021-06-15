package io.terminus.dalaran.open.core.context;


import io.terminus.dalaran.open.common.model.OpenServiceInfo;
import io.terminus.dalaran.open.common.service.DalaranOpenServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultDalaranOpenServiceContext implements DalaranOpenServiceContext {

    private final Map<String, Map<String, OpenServiceInfo>> openServiceMap = new ConcurrentHashMap<>();

    @Override
    public void registerService(String channel, String service, OpenServiceInfo serviceInfo) {

        Map<String, OpenServiceInfo> channelOpenServiceMap = openServiceMap.get(channel);
        if (MapUtils.isEmpty(channelOpenServiceMap)) {
            channelOpenServiceMap = new ConcurrentHashMap<>();
            openServiceMap.put(channel, channelOpenServiceMap);
        }
        channelOpenServiceMap.put(service, serviceInfo);
        log.info("register open service: " + channel + ", " + service);
    }

    @Override
    public OpenServiceInfo getOpenService(String channel, String service) {
        Map<String, OpenServiceInfo> channelOpenServiceMap = openServiceMap.get(channel);
        if (MapUtils.isEmpty(channelOpenServiceMap)) {
            return null;
        }
        return channelOpenServiceMap.get(service);
    }
}
