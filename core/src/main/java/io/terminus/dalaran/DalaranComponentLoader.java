package io.terminus.dalaran;

import java.util.ServiceLoader;

public class DalaranComponentLoader {

    public void loadComponents(){
        ServiceLoader.load(DalaranListener.class).forEach(l -> {
            l.getClass().getDeclaredAnnotation(DalaranComponent.class);
        });
    }

}
