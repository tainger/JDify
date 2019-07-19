package io.terminus.dalaran.spring;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranIntegration;
import okhttp3.*;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DalaranFactoryBean implements FactoryBean {

    private Class type;

    private String runtimeUrl;

    @Override
    public Object getObject() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        DalaranIntegration dalaranIntegration = (DalaranIntegration) type.getAnnotation(DalaranIntegration.class);
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            Map<String, Object> params = new HashMap<>();
            // TODO LocalVariableTable 需要编译参数, ASM 取参数名只支持 Class, 抽象类的抽象方法都不行, 暂时先取成  arg1 这种吧, 如果开启了编译参数, 还是可以获取的
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                params.put(parameters[i].getName(), args[i]);
            }
            final Request request = new Request.Builder()
                    .url(runtimeUrl + "/__dalaran-trantor/" + dalaranIntegration.key() + "/" + method.getName())
                    .post(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(params)))
                    .build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (method.getReturnType() != Void.class) {
                return JSON.parseObject(response.body().bytes(), method.getReturnType());
            }
            return null;
        };
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public void setRuntimeUrl(String runtimeUrl) {
        this.runtimeUrl = runtimeUrl;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
