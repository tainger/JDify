package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.console.entity.AlarmRuleEntity;
import io.terminus.dalaran.console.repository.AlarmRuleRepository;
import io.terminus.dalaran.console.service.AlarmRuleService;
import io.terminus.dalaran.console.util.ResourceKeyUtils;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.dto.AlarmRuleDTO;
import io.terminus.dalaran.model.query.AlarmRuleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
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
    public Long create(AlarmRuleDTO alarmRuleDTO) {
        return alarmRuleRepository.save(toEntity(alarmRuleDTO)).getId();
    }

    @Override
    public AlarmRuleDTO update(AlarmRuleDTO alarmRuleDTO) {
        alarmRuleRepository.save(toEntity(alarmRuleDTO));
        return alarmRuleDTO;
    }

    @Override
    public void delete(Long id) {
        alarmRuleRepository.deleteById(id);
    }

    @Override
    public AlarmRuleDTO detail(Long id) {
        Optional<AlarmRuleEntity> optional = alarmRuleRepository.findById(id);
        AlarmRuleEntity alarmRuleEntity = optional.get();
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

    private AlarmRuleDTO toDTO(AlarmRuleEntity alarmRuleEntity) {
        AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
        alarmRuleDTO.setModuleId(alarmRuleEntity.getModuleId());
        alarmRuleDTO.setCreateTime(alarmRuleEntity.getCreatedAt());
        alarmRuleDTO.setModifyTime(alarmRuleEntity.getUpdatedAt());
        alarmRuleDTO.setName(alarmRuleEntity.getName());
        alarmRuleDTO.setConfig(JSONObject.parseObject(alarmRuleEntity.getConfig(), Map.class));
        alarmRuleDTO.setId(alarmRuleEntity.getId());
        alarmRuleDTO.setModuleName(moduleRepository.findByIdAndIsExistTrue(alarmRuleEntity.getModuleId()).getName());
        return alarmRuleDTO;
    }

    private Specification<AlarmRuleEntity> buildSpecification(AlarmRuleQuery query) {
        return (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != query.getName()) {
                predicates.add(builder.like(root.get("name"), "%" + query.getName() + "%"));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    private AlarmRuleEntity toEntity(AlarmRuleDTO alarmRuleDTO) {
        AlarmRuleEntity alarmRuleEntity = new AlarmRuleEntity();
        alarmRuleEntity.setName(alarmRuleDTO.getName());
        alarmRuleEntity.setModuleId(alarmRuleDTO.getModuleId());
        alarmRuleEntity.setCreatedAt(new Date());
        alarmRuleEntity.setUpdatedAt(new Date());
        alarmRuleEntity.setConfig(JSONObject.toJSONString(alarmRuleDTO.getConfig()));
        alarmRuleEntity.setResourceKey(ResourceKeyUtils.generateKey());
        return alarmRuleEntity;
    }
}
