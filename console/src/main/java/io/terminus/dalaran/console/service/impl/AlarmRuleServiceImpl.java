package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.console.entity.AlarmRuleEntity;
import io.terminus.dalaran.console.entity.LimiterEntity;
import io.terminus.dalaran.console.repository.AlarmRuleRepository;
import io.terminus.dalaran.console.service.AlarmRuleService;
import io.terminus.dalaran.console.service.jpa.model.QueryAlarmRuleInfo;
import io.terminus.dalaran.console.service.jpa.model.QueryLimiterInfo;
import io.terminus.dalaran.console.util.ResourceKeyUtils;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.dto.AlarmRuleDTO;
import io.terminus.dalaran.model.dto.basic.BasicAlarmInfo;
import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;
import io.terminus.dalaran.model.query.AlarmRuleQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlarmRuleServiceImpl implements AlarmRuleService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AlarmRuleRepository alarmRuleRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public String create(AlarmRuleDTO alarmRuleDTO) {
        return alarmRuleRepository.save(toEntity(alarmRuleDTO)).getResourceKey();
    }

    @Override
    public AlarmRuleDTO update(AlarmRuleDTO alarmRuleDTO) {
        alarmRuleRepository.save(toEntity(alarmRuleDTO));
        return alarmRuleDTO;
    }

    @Override
    public void delete(String id) {
        AlarmRuleEntity entity = alarmRuleRepository.findByResourceKey(id);
        entity.setExist(false);
        alarmRuleRepository.save(entity);
    }

    @Override
    public AlarmRuleDTO detail(String id) {
        AlarmRuleEntity alarmRuleEntity = alarmRuleRepository.findByResourceKey(id);
        return toDTO(alarmRuleEntity);
    }

    @Override
    public Page<AlarmRuleDTO> queryPageable(AlarmRuleQuery query, Integer pageNumber, Integer pageSize) {
        //todo关联查询
        Sort order = new Sort(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, order);
        Page<AlarmRuleEntity> alarmRuleEntities = alarmRuleRepository.findAll(buildSpecification(query), pageable);
        return new PageImpl<>(alarmRuleEntities.stream().map(this::toDTO).collect(Collectors.toList()), pageable, alarmRuleEntities.getTotalElements());
    }

    @Override
    public List<BasicAlarmInfo> listAlarmRuleByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryAlarmRuleInfo> criteriaQuery = builder.createQuery(QueryAlarmRuleInfo.class);
        Root<AlarmRuleEntity> root = criteriaQuery.from(AlarmRuleEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"))
                .where(builder.equal(root.get("moduleId"), moduleId));
        List<QueryAlarmRuleInfo> alarmRuleInfos = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicAlarmInfo> alarmRuleInfo = new ArrayList<>();
        alarmRuleInfos.forEach(queryAlarmRule -> {
            BasicAlarmInfo basicAlarmInfo = new BasicAlarmInfo();
            try {
                BeanUtils.copyProperties(queryAlarmRule, basicAlarmInfo);
                basicAlarmInfo.setId(queryAlarmRule.getResourceKey());
                alarmRuleInfo.add(basicAlarmInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return alarmRuleInfo;
    }

    private AlarmRuleDTO toDTO(AlarmRuleEntity alarmRuleEntity) {
        AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
        alarmRuleDTO.setCreateTime(alarmRuleEntity.getCreatedAt());
        alarmRuleDTO.setModifyTime(alarmRuleEntity.getUpdatedAt());
        alarmRuleDTO.setName(alarmRuleEntity.getName());
        alarmRuleDTO.setConfig(JSONObject.parseObject(alarmRuleEntity.getConfig(), Map.class));
        alarmRuleDTO.setId(alarmRuleEntity.getResourceKey());
        return alarmRuleDTO;
    }

    private Specification<AlarmRuleEntity> buildSpecification(AlarmRuleQuery query) {
        return (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("isExit"), Boolean.TRUE));
            if (null != query.getName()) {
                predicates.add(builder.like(root.get("name"), "%" + query.getName() + "%"));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    private AlarmRuleEntity toEntity(AlarmRuleDTO alarmRuleDTO) {
        AlarmRuleEntity entity;
        String resourceKey = alarmRuleDTO.getId();
        if (!StringUtils.isBlank(resourceKey)) {
            entity = alarmRuleRepository.findByResourceKey(resourceKey);
        } else {
            entity = new AlarmRuleEntity();
            resourceKey = ResourceKeyUtils.generateKey();
        }
        entity.setResourceKey(resourceKey);
        entity.setName(alarmRuleDTO.getName());
        entity.setConfig(JSONObject.toJSONString(alarmRuleDTO.getConfig()));
        return entity;
    }
}
