package io.terminus.dalaran.core.component.config;

import org.apache.camel.impl.DefaultUuidGenerator;
import org.apache.commons.lang3.RandomUtils;

import java.net.InetAddress;

public class DalaranUuidGenerator extends DefaultUuidGenerator {

    @Override
    public String generateUuid() {
        String parentId = super.generateUuid();
        try {
            return parentId + "-" + InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return parentId + "-" + Thread.currentThread().getId() + "-" + RandomUtils.nextInt();
        }
    }
}
