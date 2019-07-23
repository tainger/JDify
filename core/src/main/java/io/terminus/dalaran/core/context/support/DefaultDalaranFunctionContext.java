package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import io.terminus.dalaran.model.function.MappingFunctionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDalaranFunctionContext implements DalaranFunctionContext {

    private final Map<String, MappingFunctionInfo> functionInfoMapper = new HashMap<>();

    private final Map<Long, Invocable> scriptFunctions = new HashMap<>();

    private final static String FUNCTION_NAME = "execute";

    @Override
    public Object executeStaticFunction(String functionKey, Object[] params) {
        MappingFunctionInfo functionInfo = functionInfoMapper.get(functionKey);
        if (functionInfo == null) {
            // TODO throw function not found
            return null;
        }
        try {
            return functionInfo.getMethod().invoke(functionInfo.getBean(), params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            // TODO throw function execute error
            return null;
        }
    }

    @Override
    public Object executeCustomFunction(Long id, Object[] params) {
        try {
            // TODO function not found exception
            return scriptFunctions.get(id).invokeFunction(FUNCTION_NAME, params);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            // TODO throw function execute error
            return null;
        }
    }

    @Override
    public void addStaticFunction(String key, String desc, Object bean, Method method) {
        MappingFunctionInfo functionInfo = new MappingFunctionInfo();
        functionInfo.setKey(key);
        functionInfo.setDescription(desc);
        functionInfo.setBean(bean);
        functionInfo.setMethod(method);
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        functionInfo.setParams(u.getParameterNames(method));
        functionInfoMapper.put(key, functionInfo);
    }

    @Override
    public void addCustomFunction(Long id, MappingFunctionType type, String script, List<String> params) {
        scriptFunctions.put(id, buildScriptEngine(type, script, params));
    }

    @Override
    public Collection<MappingFunctionInfo> allFunctionInfo() {
        return functionInfoMapper.values();
    }

    private Invocable buildScriptEngine(MappingFunctionType type, String script, List<String> params) {
        ScriptEngine engine;
        switch (type) {
            case JavaScript:
                engine = new ScriptEngineManager().getEngineByName("nashorn");
                // TODO 这部分逻辑其实前后端通用
                script = "function " + FUNCTION_NAME + " (" + StringUtils.join(params, ",") + ") {\n" + script + "\n}";
                break;
            default:
                throw new RuntimeException("unsupported script engine");
        }
        try {
            engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
            // TODO throw script error
        }
        return (Invocable) engine;
    }
}
