package io.terminus.dalaran.core.context;

import io.terminus.dalaran.model.function.MappingFunctionInfo;
import io.terminus.dalaran.model.function.MappingFunctionType;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public interface DalaranFunctionContext {

    Object executeStaticFunction(String functionKey, Object[] params);

    Object executeCustomFunction(Long id, Object[] params);

    void addStaticFunction(String key, String desc, Object bean, Method method);

    void addCustomFunction(Long id, MappingFunctionType type, String script, List<String> params);

    Collection<MappingFunctionInfo> allFunctionInfo();

}
