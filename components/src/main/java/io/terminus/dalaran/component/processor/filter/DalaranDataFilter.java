//package io.terminus.dalaran.component.processor.filter;
//
//
//import io.terminus.dalaran.core.component.DalaranProcessor;
//import io.terminus.dalaran.core.component.annotation.Processor;
//import org.apache.camel.Exchange;
//import org.apache.camel.Predicate;
//import org.apache.camel.model.ProcessorDefinition;
//
//@Processor(
//        value = "foreach",
//        order = 21,
//        configType = FilterConfig.class
//)
//public class DalaranDataFilter implements DalaranProcessor<FilterConfig> {
//
//    @Override
//    public void configure(ProcessorDefinition route, FilterConfig config) {
//        route.filter(new Predicate() {
//            @Override
//            public boolean matches(Exchange exchange) {
//                return false;
//            }
//        });
//    }
//}
