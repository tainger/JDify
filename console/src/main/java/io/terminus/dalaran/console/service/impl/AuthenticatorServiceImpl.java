package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.AuthenticatorEntity;
import io.terminus.dalaran.console.repository.AuthenticatorRepository;
import io.terminus.dalaran.console.service.AuthenticatorService;
import io.terminus.dalaran.console.service.jpa.model.QueryAuthenticatorInfo;
import io.terminus.dalaran.console.util.ResourceKeyUtils;
import io.terminus.dalaran.model.dto.AuthenticatorDTO;
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

    @Override
    public String create(AuthenticatorDTO authenticatorDTO) {
        AuthenticatorEntity entity = toEntity(authenticatorDTO);
        setCreatedBy(entity);
        repository.save(entity);
        return entity.getResourceKey();
    }

    @Override
    public AuthenticatorDTO update(AuthenticatorDTO authenticatorDTO) {
        AuthenticatorEntity entity = buildEntity(authenticatorDTO);
        repository.save(entity);
        return authenticatorDTO;
    }

    @Override
    public void delete(String authenticatorId) {
        AuthenticatorEntity entity = repository.findByResourceKey(authenticatorId);
        entity.setExist(false);
        repository.save(entity);
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
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("isExist"))
                .where(builder.equal(root.get("moduleId"), moduleId) , builder.equal(root.get("isExist"), true));
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

    private AuthenticatorEntity toEntity(AuthenticatorDTO dto) {
        AuthenticatorEntity entity = new AuthenticatorEntity();
        entity.setName(dto.getName());
        entity.setStatic(dto.isStatic());
        entity.setAuthenticatorKey(dto.getAuthenticatorKey());
        entity.setAuthenticatorValue(dto.getAuthenticatorValue());
        entity.setKeyLocation(dto.getKeyLocation());
        entity.setExpireTime(dto.getExpireTime());
        entity.setModuleId(dto.getModuleId());
        String resourceKey = dto.getId();
        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = ResourceKeyUtils.generateKey();
        }
        entity.setResourceKey(resourceKey);
        entity.setExist(true);
        return entity;
    }

    private void setCreatedBy(AuthenticatorEntity entity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            entity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private AuthenticatorEntity buildEntity(AuthenticatorDTO dto){
        AuthenticatorEntity entity = repository.findByResourceKey(dto.getId());
        entity.setName(dto.getName());
        entity.setExist(dto.isExist());
        entity.setAuthenticatorKey(dto.getAuthenticatorKey());
        entity.setAuthenticatorValue(dto.getAuthenticatorValue());
        entity.setExpireTime(dto.getExpireTime());
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null) {
            entity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
        entity.setExist(true);
        return entity;
    }

    private AuthenticatorDTO toDTO(AuthenticatorEntity entity) {
        AuthenticatorDTO dto = new AuthenticatorDTO();
        try {
            BeanUtils.copyProperties(entity, dto);
        }catch (Exception e) {
            e.printStackTrace();
        }
        dto.setName(entity.getName());
        dto.setKeyLocation(entity.getKeyLocation());
        dto.setStatic(entity.isStatic());
        dto.setAuthenticatorKey(entity.getAuthenticatorKey());
        dto.setAuthenticatorValue(entity.getAuthenticatorValue());
        dto.setExpireTime(entity.getExpireTime());
        dto.setModuleId(entity.getModuleId());
        dto.setExist(entity.isExist());
        dto.setId(entity.getResourceKey());
        return dto;
    }

}
