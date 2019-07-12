package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.core.model.function.MappingFunctionInfo;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultDalaranFunctionContext implements DalaranFunctionContext {

    private final Map<String, Method> staticFunctions = new HashMap<>();
    private final Map<String, MappingFunctionInfo> functionInfoMapper = new HashMap<>();

    private final Map<Long, Invocable> scriptFunctions = new HashMap<>();

    @Override
    public Object executeStaticFunction(String functionKey, Object[] params) {
        Method method = staticFunctions.get(functionKey);
        try {
            return method.invoke(null, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            // TODO throw function execute error
            return null;
        }
    }

    @Override
    public Object executeCustomFunction(Long id, Object[] params) {
        try {
            return scriptFunctions.get(id).invokeFunction("execute", params);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            // TODO throw function execute error
            return null;
        }
    }

    @Override
    public void addStaticFunction(String key, String desc, Method method) {
        staticFunctions.put(key, method);

        MappingFunctionInfo functionInfo = new MappingFunctionInfo();
        functionInfo.setKey(key);
        functionInfo.setDescription(desc);
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        functionInfo.setParams(u.getParameterNames(method));
        functionInfoMapper.put(key, functionInfo);
    }

    @Override
    public void addCustomFunction(Long id, String script) {
        // TODO 这里可以直接根据类型初始化不同的引擎, 暂时先 js 好了, 回头有须有再改
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(script);
            scriptFunctions.put(id, (Invocable) engine);
        } catch (ScriptException e) {
            e.printStackTrace();
            // TODO throw script error
        }
    }

    @Override
    public Collection<MappingFunctionInfo> allFunctionInfo() {
        return functionInfoMapper.values();
    }
}
