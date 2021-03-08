package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.LimiterEntity;
import io.terminus.dalaran.console.repository.LimiterRepository;
import io.terminus.dalaran.console.service.LimiterService;
import io.terminus.dalaran.console.service.jpa.model.QueryLimiterInfo;
import io.terminus.dalaran.console.util.ResourceKeyUtils;
import io.terminus.dalaran.model.dto.LimiterDTO;
import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LimiterServiceImpl implements LimiterService {

    @Autowired
    private LimiterRepository limiterRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String create(LimiterDTO limiterDTO) {
        return limiterRepository.save(toEntity(limiterDTO)).getResourceKey();
    }

    @Override
    public LimiterDTO update(LimiterDTO limiterDTO) {
        LimiterEntity entity = toEntity(limiterDTO);
        limiterRepository.save(entity);
        return limiterDTO;
    }

    @Override
    public void delete(String limiterId) {
        LimiterEntity entity = limiterRepository.findByResourceKey(limiterId);
        entity.setExist(false);
        limiterRepository.save(entity);
    }

    @Override
    public LimiterDTO detail(String limiterId) {
        LimiterEntity entity = limiterRepository.findByResourceKey(limiterId);
        return toDTO(entity);
    }

    @Override
    public List<BasicLimiterInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryLimiterInfo> criteriaQuery = builder.createQuery(QueryLimiterInfo.class);
        Root<LimiterEntity> root = criteriaQuery.from(LimiterEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("limiterType"))
                .where(builder.equal(root.get("moduleId"), moduleId));
        List<QueryLimiterInfo> limiters = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicLimiterInfo> basicLimiters = new ArrayList<>();
        limiters.forEach(limiter -> {
            BasicLimiterInfo basicLimiter = new BasicLimiterInfo();
            try {
                BeanUtils.copyProperties(basicLimiter, limiter);
                basicLimiter.setId(limiter.getResourceKey());
                basicLimiters.add(basicLimiter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicLimiters;
    }

    @Override
    public List<BasicLimiterInfo> listBasicInfoByComponent(String limiterType) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryLimiterInfo> criteriaQuery = builder.createQuery(QueryLimiterInfo.class);
        Root<LimiterEntity> root = criteriaQuery.from(LimiterEntity.class);
        Predicate where = builder.and(builder.equal(root.get("limiterType"), limiterType));
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("limiterType")).where(where);
        List<QueryLimiterInfo> limiters = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicLimiterInfo> basicLimiters = new ArrayList<>();
        limiters.forEach(limiter -> {
            BasicLimiterInfo basicLimiter = new BasicLimiterInfo();
            try {
                BeanUtils.copyProperties(basicLimiter, limiter);
                basicLimiter.setId(limiter.getResourceKey());
                basicLimiters.add(basicLimiter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicLimiters;    }

    private LimiterDTO toDTO(LimiterEntity entity) {
        LimiterDTO dto = new LimiterDTO();
        dto.setId(entity.getResourceKey());
        dto.setName(entity.getName());
        dto.setLimiterType(entity.getLimiterType());
        dto.setDescription(entity.getDescription());
        dto.setModuleId(entity.getModuleId());
        dto.setConfig(JSON.parseObject(entity.getConfig(), Map.class));
        return dto;
    }

    private LimiterEntity toEntity(LimiterDTO dto) {
        LimiterEntity entity;
        String resourceKey = dto.getId();
        if (!StringUtils.isBlank(resourceKey)) {
            entity = limiterRepository.findByResourceKey(resourceKey);
        } else {
            entity = new LimiterEntity();
            resourceKey = ResourceKeyUtils.generateKey();
        }
        entity.setResourceKey(resourceKey);
        entity.setName(dto.getName());
        entity.setLimiterType(dto.getLimiterType());
        entity.setDescription(dto.getDescription());
        entity.setModuleId(dto.getModuleId());
        entity.setConfig(JSON.toJSONString(dto.getConfig()));
        return entity;
    }
}
