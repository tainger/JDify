package io.terminus.dalaran.example;

import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.impl.DalaranCamelContext;

public class Test {

    public static void main(String[] args) throws Exception {
        DalaranContext context = new DalaranCamelContext();
        context.loadComponents();
        context.loadFlows();



        synchronized (Test.class) {
            Test.class.wait();
        }
    }
}
