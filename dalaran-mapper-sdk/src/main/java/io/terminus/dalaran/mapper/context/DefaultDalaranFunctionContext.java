package io.terminus.dalaran.mapper.context;

import io.terminus.dalaran.mapper.annotation.ContainsContextFunction;
import io.terminus.dalaran.mapper.annotation.HiddenParam;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import io.terminus.dalaran.model.function.MappingFunctionType;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang3.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DefaultDalaranFunctionContext implements DalaranFunctionContext {

    private final Map<String, MappingFunctionInfo> functionInfoMapper = new HashMap<>();

    private final Map<String, MappingFunctionInfo> scriptFunctionMapper = new HashMap<>();

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
            Object result = scriptFunctions.get(id).invokeFunction(FUNCTION_NAME, params);
            if (result instanceof ScriptObjectMirror && ((ScriptObjectMirror) result).isArray()) {
                return ((ScriptObjectMirror) result).values();
            } else {
                return result;
            }
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            // TODO throw function execute error
            return null;
        }
    }

    @Override
    public void addStaticFunction(String name, String desc, Object bean, Method method) {
        MappingFunctionInfo functionInfo = new MappingFunctionInfo();
        functionInfo.setName(name);
        functionInfo.setDescription(desc);
        functionInfo.setBean(bean);
        functionInfo.setMethod(method);

        ContainsContextFunction containsContextFunction = bean.getClass().getAnnotation(ContainsContextFunction.class);
        if (containsContextFunction != null) {
            functionInfo.setContainsContext(true);
        }

        List<String> params = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter: parameters) {
            Annotation[] annotations =  parameter.getAnnotations();
            params.add(parameter.getName());
            if (annotations != null && annotations.length > 0) {
                for (Annotation annotation: annotations) {
                    if (annotation instanceof HiddenParam) {
                        params.remove(parameter.getName());
                    }
                }
            }
        }
        functionInfo.setParams(params.toArray(new String[0]));
        functionInfoMapper.put(name, functionInfo);
    }

    @Override
    public void addCustomFunction(Long id, MappingFunctionType type, String script, List<String> params) {
        MappingFunctionInfo functionInfo = new MappingFunctionInfo();
        String functionName = String.valueOf(id);
        functionInfo.setName(functionName);
        functionInfo.setParams(params.toArray(new String[0]));
        functionInfo.setType(type);
        scriptFunctionMapper.put(functionName, functionInfo);
        scriptFunctions.put(id, buildScriptEngine(type, script, params));
    }

    @Override
    public Collection<MappingFunctionInfo> allFunctionInfo() {
        return functionInfoMapper.values();
    }

    @Override
    public MappingFunctionInfo getFunctionByKey(String key) {
        MappingFunctionInfo functionInfo = functionInfoMapper.get(key);
        if (functionInfo == null) {
            functionInfo = scriptFunctionMapper.get(key);
        }
        return functionInfo;
    }

    // TODO 可以改成自动注册
    private Invocable buildScriptEngine(MappingFunctionType type, String script, List<String> params) {
        ScriptEngine engine;
        switch (type) {
            case JavaScript:
                engine = new ScriptEngineManager().getEngineByName("nashorn");
                script = "function " + FUNCTION_NAME + " (" + StringUtils.join(params, ",") + ") {\n" + script + "\n}";
                break;
            case Groovy:
                engine = new ScriptEngineManager().getEngineByName("groovy");
                script = "def " + FUNCTION_NAME + " (" + StringUtils.join(params, ",") + ") {\n" + script + "\n}";
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
