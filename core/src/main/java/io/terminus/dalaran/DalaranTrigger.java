package io.terminus.dalaran;

public interface DalaranTrigger<T> extends Component{
    String buildRouterUri(T config);
}
