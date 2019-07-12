package io.terminus.dalaran.core.context;

import io.terminus.dalaran.core.model.function.MappingFunctionInfo;

import java.lang.reflect.Method;
import java.util.Collection;

public interface DalaranFunctionContext {

    Object executeStaticFunction(String functionKey, Object[] params);

    Object executeCustomFunction(Long id, Object[] params);

    void addStaticFunction(String key, String desc, Object bean, Method method);

    void addCustomFunction(Long id, String script);

    Collection<MappingFunctionInfo> allFunctionInfo();

}
