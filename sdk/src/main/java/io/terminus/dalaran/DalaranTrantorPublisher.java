package io.terminus.dalaran;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.model.trantor.DalaranTrantorModule;
import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Log4j
@Component
public class DalaranTrantorPublisher {

    @Value("${trantor.dalaran.consoleUrl}")
    private String consoleUrl;

    public void publish(DalaranTrantorModule trantorModule) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        final Request request = new Request.Builder()
                .url(consoleUrl + "/api/platform/trantor")
                .post(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(trantorModule)))
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            log.info("Publish dalaran integration info");
        } else {
            log.error("Publish dalaran integration info fail, message: " + response.message());
        }
    }
}
