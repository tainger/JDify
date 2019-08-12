package io.terminus.dalaran.component.processor.sgm;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.processor.sgm.model.SGMSignInfo;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SGMHttpProcessor implements Processor {

    private SGMHttpClientConfig config;

    private RedisTemplate<String, SGMSignInfo> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SGMHttpProcessor.class);

    private static final String HTTP_URI = "%s://%s:%s%s?appid=%s&ACCESS_TOKEN=%s&sno=%s";

    public SGMHttpProcessor(SGMHttpClientConfig config, RedisTemplate<String, SGMSignInfo> redisTemplate) {
        this.config = config;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        SGMHttpClientConnector connector = config.getConnector();
        String key = connector.getAppId() + "-" + connector.getSno();
        SGMSignInfo signInfo = redisTemplate.hasKey(key) ? redisTemplate.opsForValue().get(key) : getAccessToken(connector, key);
        String accessToken = signInfo.getAccessToken();
        String timestamp = signInfo.getTimestamp();
        String sign = calculationSign(connector.getToken(), timestamp);
        String uri = String.format(HTTP_URI, connector.getProtocol().name().toLowerCase(), connector.getHost(),
                connector.getPort(), config.getPath(), connector.getAppId(), accessToken, connector.getSno());
        Long timeout = connector.getTimeout();
        Object data = request(uri, timestamp, sign, exchange.getIn().getBody(), timeout);
        exchange.getOut().setBody(data);
    }

    private SGMSignInfo getAccessToken(SGMHttpClientConnector connector, String key) throws IOException {
        String url = connector.getAuthUrl() + "?appid=" + connector.getAppId() + "&secret=" + connector.getSecret() + "&sno=" + connector.getSno();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                Map<String, Object> rst = JSON.parseObject(responseBody.string(), Map.class);
                String accessToken = rst.get("access_token").toString();
                String timestamp = rst.get("timestamp").toString();
                SGMSignInfo signInfo = new SGMSignInfo(accessToken, timestamp);
                redisTemplate.opsForValue().set(key, signInfo, connector.getTokenTimeout(), TimeUnit.SECONDS);
                logger.info("get access token success! token: " + accessToken);
                return signInfo;
            }
        }
        logger.warn("get access token failure, access token is null!");
        return new SGMSignInfo();
    }

    private Object request(String uri, String timestamp, String sign, Object body, Long timeout) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(timeout, TimeUnit.SECONDS).build();
        Map<String, Object> params = new HashMap<>();
        params.put("sign", sign);
        params.put("timestamp", timestamp);
        params.put("params", body);
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(params), MediaType.parse("application/json"));
        logger.info("request uri: " + uri);
        logger.info("request params: " + params);
        Request request = new Request.Builder().url(uri).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                Map<String, Object> rst = JSON.parseObject(responseBody.string(), Map.class);
                return rst.get("data");
            }
        }
        logger.warn("response body is null!");
        return null;
    }

    private String calculationSign(String accessToken, String timestamp) {
        List<String> params = Arrays.asList(accessToken, timestamp);
        Collections.sort(params);
        return DigestUtils.sha1Hex(String.join("", params));
    }
}
