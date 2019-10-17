package io.terminus.dalaran.core.resource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public interface DalaranStarter extends ApplicationListener<ApplicationReadyEvent> {

    @Override
    default void onApplicationEvent(ApplicationReadyEvent event) {
        start();
    }

    void start();

    default void stop() {

    }
}
