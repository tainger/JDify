package io.terminus.dalaran.component.processor.http;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.utils.SignUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class HttpClientSignProcessor implements Processor {

    private String apiSecret;

    public HttpClientSignProcessor(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        JsonObject jsonObject;
        Gson gson = new Gson();
        if (body instanceof byte[]) {
            jsonObject = gson.toJsonTree(JSON.parse((byte[])body)).getAsJsonObject();
        } else if (body instanceof String) {
            jsonObject = gson.toJsonTree(JSON.parseObject((String)body)).getAsJsonObject();
        } else {
            jsonObject = gson.toJsonTree(body).getAsJsonObject();
        }
        String sign = SignUtils.calculateMD5Signature(jsonObject, "");
        jsonObject.addProperty(ComponentConstants.SIGNATURE, sign);
    }
}
