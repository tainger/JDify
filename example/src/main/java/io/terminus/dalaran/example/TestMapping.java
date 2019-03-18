package io.terminus.dalaran.example;

import io.terminus.dalaran.*;
import io.terminus.dalaran.impl.DalaranCamelContext;

/**
 * Created by jingdi on 2019/3/13
 */
public class TestMapping {

    public static void main(String[] args) throws Exception {
        DalaranContext context = new DalaranCamelContext();
        context.loadComponents();
        context.loadFlows();

        synchronized (Test.class) {
            Test.class.wait();
        }
    }
}
