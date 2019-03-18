package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.model.DalaranComponentInstance;

import java.util.List;

public class DalaranRouterConfig {
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    class Route {
        private String when;

        private DalaranComponentInstance processor;

        public String getWhen() {
            return when;
        }

        public void setWhen(String when) {
            this.when = when;
        }

        public DalaranComponentInstance getProcessor() {
            return processor;
        }

        public void setProcessor(DalaranComponentInstance processor) {
            this.processor = processor;
        }
    }

}
