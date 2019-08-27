package io.terminus.dalaran.component.processor.sgm;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.processor.sgm.model.SGMConstants;
import io.terminus.dalaran.component.processor.sgm.model.SGMSignInfo;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.terminus.dalaran.component.processor.sgm.model.SGMConstants.SUCCESSFUL_CODE;

public class SGMHttpProcessor implements Processor, Traceable {

    private SGMHttpClientConfig config;

    private RedisTemplate<String, SGMSignInfo> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SGMHttpProcessor.class);

    private static final String HTTP_URI = "%s://%s%s?appid=%s&ACCESS_TOKEN=%s&sno=%s";

    public SGMHttpProcessor(SGMHttpClientConfig config, RedisTemplate<String, SGMSignInfo> redisTemplate) {
        this.config = config;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        SGMHttpClientConnector connector = config.getConnector();
        String key = connector.getAppId() + "-" + connector.getSno();
        String host = formatHost(connector.getHost());
        SGMSignInfo signInfo = redisTemplate.hasKey(key) ? redisTemplate.opsForValue().get(key) : getAccessToken(connector, key, host);
        String accessToken = signInfo.getAccessToken();
        String timestamp = signInfo.getTimestamp();
        String sign = calculationSign(connector.getToken(), timestamp);
        String uri = String.format(HTTP_URI, SGMConstants.PROTOCOL_HTTP, host,
                SGMConstants.COMMAND_ROOT + config.getCommand(), connector.getAppId(), accessToken, connector.getSno());
        Long timeout = connector.getTimeout();
        Object data = request(uri, timestamp, sign, exchange.getIn().getBody(), timeout);
        exchange.getOut().setBody(data);
    }

    private SGMSignInfo getAccessToken(SGMHttpClientConnector connector, String key, String host) throws IOException {
        String url = SGMConstants.PROTOCOL_HTTP + "://" + host + SGMConstants.GET_TOKEN + "?appid=" + connector.getAppId() + "&secret=" + connector.getSecret() + "&sno=" + connector.getSno();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                Map<String, Object> rst = JSON.parseObject(responseBody.string(), Map.class);
                String accessToken = rst.get(SGMConstants.ACCESS_TOKEN).toString();
                String timestamp = rst.get(SGMConstants.TIMESTAMP).toString();
                SGMSignInfo signInfo = new SGMSignInfo(accessToken, timestamp);
                redisTemplate.opsForValue().set(key, signInfo, 60, TimeUnit.SECONDS);
                logger.info("get access token success!");
                return signInfo;
            }
        }
        logger.warn("get access token failure, access token is null!");
        return new SGMSignInfo();
    }

    private Object request(String uri, String timestamp, String sign, Object body, Long timeout) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(timeout, TimeUnit.SECONDS).build();
        Map<String, Object> params = new HashMap<>();
        params.put(SGMConstants.SIGN, sign);
        params.put(SGMConstants.TIMESTAMP, timestamp);
        params.put(SGMConstants.PARAMS, body);
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(params), MediaType.parse("application/json"));
        Request request = new Request.Builder().url(uri).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                Map<String, Object> rst = JSON.parseObject(responseBody.string(), Map.class);
                Object code = rst.get(SGMConstants.RESPONSE_CODE);
                if (SUCCESSFUL_CODE.equals(code)) {
                    return rst.get(SGMConstants.RESPONSE_DATA);
                }
                throw new SGMHttpClientException("SGM business error code [" + code + "], message: " + rst.get(SGMConstants.RESPONSE_MESSAGE));
            }
        }
        logger.warn("response body is null!");
        throw new SGMHttpClientException("GSM client request error");
    }

    private String calculationSign(String accessToken, String timestamp) {
        List<String> params = Arrays.asList(accessToken, timestamp);
        Collections.sort(params);
        return DigestUtils.sha1Hex(String.join("", params));
    }

    private String formatHost(String host) {
        if (StringUtils.startsWith(host, SGMConstants.PROTOCOL_HTTP) || StringUtils.startsWith(host, SGMConstants.PROTOCOL_HTTPS)) {
            return StringUtils.substringAfter(host, "://");
        }
        return host;
    }

    @Override
    public String getTraceLabel() {
        return "SGM client";
    }
}
