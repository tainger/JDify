package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.ConnectorEntity;
import io.terminus.dalaran.console.repository.ConnectorRepository;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
import io.terminus.draco.web.autoconfig.context.UserContext;
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
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Long create(ConnectorDTO connectorDTO) {
        ConnectorEntity entity = toEntity(connectorDTO);
        setCreatedBy(entity);
        connectorRepository.save(entity);
        return entity.getId();
    }

    @Override
    public ConnectorDTO update(ConnectorDTO connectorDTO) {
        ConnectorEntity connectorEntity = buildEntity(connectorDTO);
        connectorRepository.save(connectorEntity);
        return connectorDTO;
    }

    @Override
    public void delete(Long connectorId) {
        ConnectorEntity entity = connectorRepository.findById(connectorId).get();
        entity.setExist(false);
        connectorRepository.save(entity);
    }

    @Override
    public ConnectorDTO detail(Long connectorId) {
        Optional<ConnectorEntity> entityOptional = connectorRepository.findById(connectorId);
        return entityOptional.map(this::toDTO).orElse(null);
    }

    @Override
    public List<BasicConnectorInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicConnectorInfo> criteriaQuery = builder.createQuery(BasicConnectorInfo.class);
        Root<ConnectorEntity> root = criteriaQuery.from(ConnectorEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("connectorType"))
                .where(builder.equal(root.get("moduleId"), moduleId), builder.equal(root.get("isExist"),true));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<BasicConnectorInfo> listBasicInfoByComponent(String connectorType) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicConnectorInfo> criteriaQuery = builder.createQuery(BasicConnectorInfo.class);
        Root<ConnectorEntity> root = criteriaQuery.from(ConnectorEntity.class);
        Predicate where = builder.and(builder.equal(root.get("connectorType"), connectorType), builder.equal(root.get("isExist"),true));
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("connectorType")).where(where);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private ConnectorDTO toDTO(ConnectorEntity entity) {
        ConnectorDTO dto = new ConnectorDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setConnectorType(entity.getConnectorType());
        dto.setDescription(entity.getDescription());
        dto.setModuleId(entity.getModuleId());
        dto.setConfig(JSON.parseObject(entity.getConfig(), Map.class));
        dto.setExist(true);
        return dto;
    }

    private ConnectorEntity toEntity(ConnectorDTO dto) {
        ConnectorEntity entity = new ConnectorEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setConnectorType(dto.getConnectorType());
        entity.setDescription(dto.getDescription());
        entity.setModuleId(dto.getModuleId());
        entity.setConfig(JSON.toJSONString(dto.getConfig()));
        entity.setExist(true);
        return entity;
    }

    private void setCreatedBy(ConnectorEntity connectorEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            connectorEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private ConnectorEntity buildEntity(ConnectorDTO connectorDTO){
        ConnectorEntity connectorEntity = connectorRepository.findById(connectorDTO.getId()).get();
        connectorEntity.setModuleId(connectorDTO.getModuleId());
        connectorEntity.setName(connectorDTO.getName());
        connectorEntity.setConnectorType(connectorDTO.getConnectorType());
        connectorEntity.setDescription(connectorDTO.getDescription());
        connectorEntity.setConfig(JSON.toJSONString(connectorDTO.getConfig()));
        if(UserContext.getUserInfo().getUsername()!=null){
            connectorEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
        return connectorEntity;
    }

}
