package io.terminus.dalaran.core.component.config;

import org.apache.camel.impl.DefaultUuidGenerator;

import java.util.Random;

public class DalaranUuidGenerator extends DefaultUuidGenerator {

    @Override
    public String generateUuid() {
        String parentId = super.generateUuid();
        return parentId + "-" + Thread.currentThread().getId() + "-" + new Random().nextInt();
    }
}
