package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.ComponentType;
import io.terminus.dalaran.console.entity.ConnectorEntity;
import io.terminus.dalaran.console.repository.ConnectorRepository;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Long create(ConnectorDTO connectorDTO) {
        ConnectorEntity entity = toEntity(connectorDTO);
        connectorRepository.save(entity);
        return entity.getId();
    }

    @Override
    public ConnectorDTO update(ConnectorDTO connectorDTO) {
        ConnectorEntity entity = toEntity(connectorDTO);
        connectorRepository.save(entity);
        return toDTO(entity);
    }

    @Override
    public void delete(Long connectorId) {
        connectorRepository.deleteById(connectorId);
    }

    @Override
    public ConnectorDTO detail(Long connectorId) {
        ConnectorEntity entity = connectorRepository.findById(connectorId).get();
        if (entity != null) {
            return toDTO(entity);
        }
        return null;
    }

    @Override
    public List<BasicConnectorInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicConnectorInfo> criteriaQuery = builder.createQuery(BasicConnectorInfo.class);
        Root<ConnectorEntity> root = criteriaQuery.from(ConnectorEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("componentType"), root.get("componentName"))
                .where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<BasicConnectorInfo> listBasicInfoByComponent(ComponentType componentType, String componentName) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicConnectorInfo> criteriaQuery = builder.createQuery(BasicConnectorInfo.class);
        Root<ConnectorEntity> root = criteriaQuery.from(ConnectorEntity.class);
        Predicate where = builder.and(builder.equal(root.get("componentType"), componentType), builder.equal(root.get("componentType"), componentType));
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("componentType"),
                root.get("componentName")).where(where);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private ConnectorDTO toDTO(ConnectorEntity entity) {
        ConnectorDTO dto = new ConnectorDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setComponentType(entity.getComponentType());
        dto.setComponentName(entity.getComponentName());
        dto.setDescription(entity.getDescription());
        dto.setModuleId(entity.getModuleId());
        dto.setConfig(JSON.parseObject(entity.getConfig(), Map.class));
        return dto;
    }

    private ConnectorEntity toEntity(ConnectorDTO dto) {
        ConnectorEntity entity = new ConnectorEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setComponentType(dto.getComponentType());
        entity.setComponentName(dto.getComponentName());
        entity.setDescription(dto.getDescription());
        entity.setModuleId(dto.getModuleId());
        entity.setConfig(JSON.toJSONString(dto.getConfig()));
        return entity;
    }

}
