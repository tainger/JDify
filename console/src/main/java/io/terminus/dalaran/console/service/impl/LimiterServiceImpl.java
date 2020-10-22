package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.LimiterEntity;
import io.terminus.dalaran.console.repository.LimiterRepository;
import io.terminus.dalaran.console.service.LimiterService;
import io.terminus.dalaran.model.dto.LimiterDTO;
import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LimiterServiceImpl implements LimiterService {

    @Autowired
    private LimiterRepository limiterRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Long create(LimiterDTO limiterDTO) {
        return limiterRepository.save(toEntity(limiterDTO)).getId();
    }

    @Override
    public LimiterDTO update(LimiterDTO limiterDTO) {
        LimiterEntity entity = toEntity(limiterDTO);
        limiterRepository.save(entity);
        return limiterDTO;
    }

    @Override
    public void delete(Long limiterId) {
        limiterRepository.deleteById(limiterId);
    }

    @Override
    public LimiterDTO detail(Long limiterId) {
        Optional<LimiterEntity> entityOptional = limiterRepository.findById(limiterId);
        return entityOptional.map(this::toDTO).orElse(null);
    }

    @Override
    public List<BasicLimiterInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicLimiterInfo> criteriaQuery = builder.createQuery(BasicLimiterInfo.class);
        Root<LimiterEntity> root = criteriaQuery.from(LimiterEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("limiterType"))
                .where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<BasicLimiterInfo> listBasicInfoByComponent(String limiterType) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicLimiterInfo> criteriaQuery = builder.createQuery(BasicLimiterInfo.class);
        Root<LimiterEntity> root = criteriaQuery.from(LimiterEntity.class);
        Predicate where = builder.and(builder.equal(root.get("limiterType"), limiterType));
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("limiterType")).where(where);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private LimiterDTO toDTO(LimiterEntity entity) {
        LimiterDTO dto = new LimiterDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLimiterType(entity.getLimiterType());
        dto.setDescription(entity.getDescription());
        dto.setModuleId(entity.getModuleId());
        dto.setConfig(JSON.parseObject(entity.getConfig(), Map.class));
        return dto;
    }

    private LimiterEntity toEntity(LimiterDTO dto) {
        LimiterEntity entity;
        if (dto.getId() != null) {
            entity = limiterRepository.findById(dto.getId()).get();
        } else {
            entity = new LimiterEntity();
        }
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLimiterType(dto.getLimiterType());
        entity.setDescription(dto.getDescription());
        entity.setModuleId(dto.getModuleId());
        entity.setConfig(JSON.toJSONString(dto.getConfig()));
        return entity;
    }
}
