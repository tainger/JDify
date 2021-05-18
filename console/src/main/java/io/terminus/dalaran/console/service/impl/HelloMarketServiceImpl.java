package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.DalaranConsoleConstants;
import io.terminus.dalaran.console.entity.TenantKeyEntity;
import io.terminus.dalaran.console.model.BasicResponse;
import io.terminus.dalaran.console.repository.TenantKeyRepository;
import io.terminus.dalaran.console.service.HelloMarketService;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.core.resource.property.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class HelloMarketServiceImpl implements HelloMarketService, InitializingBean {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private TenantKeyRepository tenantKeyRepository;

    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public void afterPropertiesSet() throws Exception {
        registerMarket();
    }

    @Override
    public void registerMarket() {
        String tenantCode = propertyService.getTenantCode();
        String tenantConsoleUrl = propertyService.getTenantConsoleUrl();
        String resourceKey = getResourcekey();
        HashMap<String, Object> request = new HashMap();
        request.put(DalaranConsoleConstants.REGISTER_ATTRIBUTE_NAME, tenantCode);
        request.put(DalaranConsoleConstants.REMOTE_CONSOLE_URL, tenantConsoleUrl);
        request.put(DalaranConsoleConstants.ID, resourceKey);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map> requestEntity = new HttpEntity<>(request, requestHeaders);
        ResponseEntity<BasicResponse> response = restTemplate.postForEntity(propertyService.getMarketHost() + propertyService.getRegister(), requestEntity, BasicResponse.class);
        if(response.getStatusCode() != HttpStatus.OK) {
            log.error("注册市场失败，原因: {}", response.getBody());
            return;
        }
        BasicResponse basicResponse = response.getBody();
        if(!basicResponse.isSuccess()){
            log.error("注册市场失败，原因: {}", response.getBody());
        }
    }

    //  when distribute env may be in danger.
    private String getResourcekey() {
        List<TenantKeyEntity> tenantKeyEntities = tenantKeyRepository.findAll();
        if (CollectionUtils.isEmpty(tenantKeyEntities)) {
            String resourceKey = GenerateKeyUtils.resourceKey();
            TenantKeyEntity tenantKeyEntity = new TenantKeyEntity();
            tenantKeyEntity.setResourceKey(resourceKey);
            tenantKeyRepository.save(tenantKeyEntity);
            return resourceKey;
        }
        return tenantKeyEntities.get(0).getResourceKey();
    }
}
