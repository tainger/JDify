package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.DalaranConsoleConstants;
import io.terminus.dalaran.console.entity.AuthenticatorEntity;
import io.terminus.dalaran.console.repository.AuthenticatorRepository;
import io.terminus.dalaran.console.service.AuthenticatorService;
import io.terminus.dalaran.console.service.jpa.model.QueryAuthenticatorInfo;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.model.AuthenticatorKeyResponse;
import io.terminus.dalaran.model.AuthenticatorValueResponse;
import io.terminus.dalaran.model.dto.BasicAuthenticatorConfigDTO;
import io.terminus.dalaran.model.dto.AuthenticatorDTO;
import io.terminus.dalaran.model.dto.SignAuthenticatorConfigDTO;
import io.terminus.dalaran.model.dto.basic.BasicAuthenticatorInfo;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticatorServiceImpl implements AuthenticatorService {

    @Autowired
    AuthenticatorRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RedisService redisService;

    @Override
    public String create(AuthenticatorDTO authenticatorDTO) {
        AuthenticatorEntity entity = toEntity(authenticatorDTO);
        setCreatedBy(entity);
        repository.save(entity);
        if (authenticatorDTO.getAuthenticatorType().equals("BasicAuthenticator")) {
            saveToRedis(JSON.parseArray(JSON.toJSONString(authenticatorDTO.getConfig()), BasicAuthenticatorConfigDTO.class));
        }
        return entity.getResourceKey();
    }

    @Override
    public AuthenticatorDTO update(AuthenticatorDTO authenticatorDTO) {
        AuthenticatorEntity entity = buildEntity(authenticatorDTO);
        repository.save(entity);
        if (authenticatorDTO.getAuthenticatorType().equals("BasicAuthenticator")) {
            saveToRedis(JSON.parseArray(JSON.toJSONString(authenticatorDTO.getConfig()), BasicAuthenticatorConfigDTO.class));
        }
        return authenticatorDTO;
    }

    @Override
    public void delete(String authenticatorId) {
        AuthenticatorEntity entity = repository.findByResourceKey(authenticatorId);
        entity.setExist(false);
        repository.save(entity);
        if (entity.getAuthenticatorType().equals("BasicAuthenticator")) {
            List<BasicAuthenticatorConfigDTO> basicAuthenticatorConfigDTOS = JSONObject.parseArray(entity.getConfig(), BasicAuthenticatorConfigDTO.class);
            deleteFromRedis(basicAuthenticatorConfigDTOS);
        }
    }

    @Override
    public AuthenticatorDTO detail(String authenticatorId) {
        AuthenticatorEntity entity = repository.findByResourceKey(authenticatorId);
        if (entity != null) {
            return toDTO(entity);
        }
        return null;
    }

    @Override
    public List<BasicAuthenticatorInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryAuthenticatorInfo> criteriaQuery = builder.createQuery(QueryAuthenticatorInfo.class);
        Root<AuthenticatorEntity> root = criteriaQuery.from(AuthenticatorEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("authenticatorType"),
                root.get("isExist")).where(builder.equal(root.get("moduleId"), moduleId), builder.equal(root.get("isExist"), true));
        List<QueryAuthenticatorInfo> authenticators = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicAuthenticatorInfo> basicAuthenticatorInfos = new ArrayList<>();
        authenticators.forEach(authenticator -> {
            BasicAuthenticatorInfo basicAuthenticatorInfo = new BasicAuthenticatorInfo();
            try {
                BeanUtils.copyProperties(basicAuthenticatorInfo, authenticator);
                basicAuthenticatorInfo.setId(authenticator.getResourceKey());
                basicAuthenticatorInfos.add(basicAuthenticatorInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicAuthenticatorInfos;
    }

    @Override
    public AuthenticatorKeyResponse getKey() {
        return new AuthenticatorKeyResponse(GenerateKeyUtils.authenticatorKey());
    }

    @Override
    public AuthenticatorValueResponse getValue(String key) {
        String value = GenerateKeyUtils.authenticatorValue();
        String expireTimeValue = redisService.getValue(DalaranConsoleConstants.REDIS_EXPIRETIME_KEY + key);
        if (StringUtils.isBlank(expireTimeValue)) {
            return new AuthenticatorValueResponse("invalid key");
        }
        redisService.setValueMinutes(DalaranConsoleConstants.REDIS_AUTHENTICATOR_KEY + key, value, Long.parseLong(expireTimeValue));
        return new AuthenticatorValueResponse(value);
    }

    private AuthenticatorEntity toEntity(AuthenticatorDTO dto) {
        AuthenticatorEntity entity = new AuthenticatorEntity();
        entity.setName(dto.getName());
        entity.setAuthenticatorType(dto.getAuthenticatorType());
        entity.setConfig(JSON.toJSONString(dto.getConfig()));
        entity.setModuleId(dto.getModuleId());
        String resourceKey = dto.getId();
        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = GenerateKeyUtils.resourceKey();
        }
        entity.setResourceKey(resourceKey);
        entity.setExist(true);
        return entity;
    }

    private void setCreatedBy(AuthenticatorEntity entity) {
        if (UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null) {
            entity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private AuthenticatorEntity buildEntity(AuthenticatorDTO dto) {
        AuthenticatorEntity entity = repository.findByResourceKey(dto.getId());
        entity.setName(dto.getName());
        entity.setAuthenticatorType(dto.getAuthenticatorType());
        entity.setExist(dto.isExist());
        entity.setConfig(JSON.toJSONString(dto.getConfig()));
        if (UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null) {
            entity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
        entity.setExist(true);
        return entity;
    }

    private AuthenticatorDTO toDTO(AuthenticatorEntity entity) {
        AuthenticatorDTO dto = new AuthenticatorDTO();
        dto.setName(entity.getName());
        dto.setAuthenticatorType(entity.getAuthenticatorType());
        if (entity.getAuthenticatorType().equals("BasicAuthenticator")) {
            dto.setConfig(JSONObject.parseArray(entity.getConfig(), BasicAuthenticatorConfigDTO.class));
        } else if (entity.getAuthenticatorType().equals("SignAuthenticator")) {
            dto.setConfig(JSONObject.parseArray(entity.getConfig(), SignAuthenticatorConfigDTO.class));
        }
        dto.setModuleId(entity.getModuleId());
        dto.setExist(entity.isExist());
        dto.setId(entity.getResourceKey());
        return dto;
    }

    private void saveToRedis(List<BasicAuthenticatorConfigDTO> dtos) {
        dtos.forEach(dto -> {
            if (dto.getIsStatic()) {
                redisService.persistKey(DalaranConsoleConstants.REDIS_AUTHENTICATOR_KEY + dto.getAuthenticatorKey(), dto.getAuthenticatorValue());
            } else {
                redisService.persistKey(DalaranConsoleConstants.REDIS_EXPIRETIME_KEY + dto.getAuthenticatorKey(), String.valueOf(dto.getExpireTime()));
            }
        });
    }

    private void deleteFromRedis(List<BasicAuthenticatorConfigDTO> dtos) {
        dtos.forEach(dto -> {
            if (dto.getIsStatic()) {
                redisService.deleteKey(DalaranConsoleConstants.REDIS_AUTHENTICATOR_KEY + dto.getAuthenticatorKey());
            } else {
                redisService.deleteKey(DalaranConsoleConstants.REDIS_EXPIRETIME_KEY + dto.getAuthenticatorKey());
            }
        });
    }


}
