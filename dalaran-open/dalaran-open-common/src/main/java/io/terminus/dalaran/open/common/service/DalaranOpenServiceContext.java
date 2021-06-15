package io.terminus.dalaran.open.common.service;

import io.terminus.dalaran.open.common.model.OpenServiceInfo;

public interface DalaranOpenServiceContext {

    void registerService(String channel, String service, OpenServiceInfo serviceInfo);

    OpenServiceInfo getOpenService(String channel, String service);

}
