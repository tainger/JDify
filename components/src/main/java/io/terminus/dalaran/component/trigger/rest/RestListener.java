package io.terminus.dalaran.component.trigger.rest;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import io.swagger.models.Swagger;
import io.terminus.dalaran.component.trigger.rest.model.ApiInfo;
import io.terminus.dalaran.component.trigger.rest.model.SignAuthenticatorInfo;
import io.terminus.dalaran.component.trigger.rest.processor.QueryStringConvertProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.QueryStringSignProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.SignProcessor;
import io.terminus.dalaran.component.trigger.rest.utils.RestWordUtils;
import io.terminus.dalaran.component.trigger.rest.utils.SwaggerUtils;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerApiDocExport;
import io.terminus.dalaran.core.component.DalaranTriggerWordDocExport;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.context.DalaranClientContext;
import io.terminus.dalaran.core.resource.oss.OSSAccount;
import io.terminus.dalaran.model.flow.TriggerFlow;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Trigger(
        value = {"http-rest-listener", "netty-http-listener"},
        order = 10,
        configType = RestConfig.class,
        bodyType = "JSON"
)
public class RestListener implements DalaranTrigger<RestConfig>, DalaranTriggerApiDocExport<Swagger>, DalaranTriggerWordDocExport {

    @Autowired
    private DalaranClientContext clientContext;

    @Autowired
    private OSSAccount ossAccount;

    @Override
    public void buildFromRoute(RouteDefinition route, RestConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?chunkedMaxContentLength=104857600&httpMethodRestrict=" + config.getMethod();
        route.from(uri);
        SignAuthenticatorInfo signAuthenticatorInfo = authenticatorConfig(ossAccount, config);
        if (config.getMethod().isNoBody()) {
            if (config.isEnableSign()) {
                route.process(new QueryStringSignProcessor(clientContext.getAllClient(), signAuthenticatorInfo));
            } else {
                route.process(new QueryStringConvertProcessor());
            }
            // TODO 目前会多一次序列化, 如果下个节点要求的是非序列化对象, 会有额外的性能开销
            route.marshal().json(JsonLibrary.Fastjson);
        } else {
            if (config.isEnableSign()) {
                route.unmarshal().json(JsonLibrary.Fastjson);
                route.process(new SignProcessor(clientContext.getAllClient(), signAuthenticatorInfo));
            } else {
                // TODO Stream to string
                route.convertBodyTo(String.class);
            }
        }
    }

    @Override
    public Swagger exportApiDoc(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return SwaggerUtils.buildSwagger(buildApiInfoList(moduleTriggerFlows));
    }

    @Override
    public File exportWord(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return RestWordUtils.buildWordFile(buildApiInfoList(moduleTriggerFlows));
    }

    private List<ApiInfo> buildApiInfoList(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return moduleTriggerFlows.entrySet().stream().flatMap(module ->
                module.getValue().stream().map(flow -> new ApiInfo(module.getKey(), flow))
        ).collect(Collectors.toList());
    }

    private SignAuthenticatorInfo authenticatorConfig(OSSAccount ossAccount, RestConfig config) {
        SignAuthenticatorInfo authenticatorInfo = new SignAuthenticatorInfo();
        authenticatorInfo.setEncryptionAlgorithm(config.getEncryptionAlgorithm());
        authenticatorInfo.setSignAlgorithm(config.getSignAlgorithm());
        OSS client = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        InputStream dalaranPublicStream = client.getObject(new GetObjectRequest(ossAccount.getBucketName(), config.getDalaranPublicKey())).getObjectContent();
        InputStream dalaranPrivateStream = client.getObject(new GetObjectRequest(ossAccount.getBucketName(), config.getDalaranPrivateKey())).getObjectContent();
        InputStream partnerPublicStream = client.getObject(new GetObjectRequest(ossAccount.getBucketName(), config.getPartnerPublicKey())).getObjectContent();
        try {
            authenticatorInfo.setDalaranPublicKey(IOUtils.toString(dalaranPublicStream, StandardCharsets.UTF_8).replace("\n", ""));
            authenticatorInfo.setDalaranPrivateKey(IOUtils.toString(dalaranPrivateStream, StandardCharsets.UTF_8).replace("\n", ""));
            authenticatorInfo.setPartnerPublicKey(IOUtils.toString(partnerPublicStream, StandardCharsets.UTF_8).replace("\n", ""));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dalaranPublicStream.close();
                client.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return authenticatorInfo;
    }
}
