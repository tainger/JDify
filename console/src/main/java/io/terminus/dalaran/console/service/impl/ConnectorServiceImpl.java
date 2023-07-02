package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.ConnectorEntity;
import io.terminus.dalaran.console.model.UserContext;
import io.terminus.dalaran.console.repository.ConnectorRepository;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.console.service.jpa.model.QueryConnectorInfo;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
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
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String create(ConnectorDTO connectorDTO) {
        ConnectorEntity entity = toEntity(connectorDTO);
        setCreatedBy(entity);
        connectorRepository.save(entity);
        return entity.getResourceKey();
    }

    @Override
    public ConnectorDTO update(ConnectorDTO connectorDTO) {
        ConnectorEntity connectorEntity = buildEntity(connectorDTO);
        connectorRepository.save(connectorEntity);
        return connectorDTO;
    }

    @Override
    public void delete(String connectorId) {
        ConnectorEntity entity = connectorRepository.findByResourceKey(connectorId);
        entity.setExist(false);
        connectorRepository.save(entity);
    }

    @Override
    public ConnectorDTO detail(String connectorId) {
        ConnectorEntity entity = connectorRepository.findByResourceKey(connectorId);
        return toDTO(entity);
    }

    @Override
    public List<BasicConnectorInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryConnectorInfo> criteriaQuery = builder.createQuery(QueryConnectorInfo.class);
        Root<ConnectorEntity> root = criteriaQuery.from(ConnectorEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("nodeId"), root.get("moduleId"), root.get("name"), root.get("connectorType"), root.get("isExist"))
                .where(builder.equal(root.get("moduleId"), moduleId), builder.equal(root.get("isExist"),true));
        List<QueryConnectorInfo> connectors = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicConnectorInfo> basicConnectors = new ArrayList<>();
        connectors.forEach(connector -> {
            BasicConnectorInfo basicConnector = new BasicConnectorInfo();
            try {
                BeanUtils.copyProperties(basicConnector, connector);
                basicConnector.setId(connector.getResourceKey());
                basicConnectors.add(basicConnector);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicConnectors;
    }

    @Override
    public List<BasicConnectorInfo> listBasicInfoByComponent(String connectorType) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryConnectorInfo> criteriaQuery = builder.createQuery(QueryConnectorInfo.class);
        Root<ConnectorEntity> root = criteriaQuery.from(ConnectorEntity.class);
        Predicate where = builder.and(builder.equal(root.get("connectorType"), connectorType), builder.equal(root.get("isExist"),true));
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("nodeId"), root.get("moduleId"), root.get("name"), root.get("connectorType")).where(where);
        List<QueryConnectorInfo> connectors = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicConnectorInfo> basicConnectors = new ArrayList<>();
        connectors.forEach(connector -> {
            BasicConnectorInfo basicConnector = new BasicConnectorInfo();
            try {
                BeanUtils.copyProperties(basicConnector, connector);
                basicConnector.setId(connector.getResourceKey());
                basicConnectors.add(basicConnector);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicConnectors;
    }

    private ConnectorDTO toDTO(ConnectorEntity entity) {
        ConnectorDTO dto = new ConnectorDTO();
        if (entity == null) {
            return dto;
        }
        dto.setId(entity.getResourceKey());
        dto.setNodeId(entity.getNodeId());
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
        entity.setName(dto.getName());
        String resourceKey = dto.getId();
        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = GenerateKeyUtils.resourceKey();
        }
        entity.setResourceKey(resourceKey);
        entity.setNodeId(dto.getNodeId());
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
        ConnectorEntity connectorEntity = connectorRepository.findByResourceKey(connectorDTO.getId());
        connectorEntity.setNodeId(connectorDTO.getNodeId());
        connectorEntity.setModuleId(connectorDTO.getModuleId());
        connectorEntity.setName(connectorDTO.getName());
        connectorEntity.setConnectorType(connectorDTO.getConnectorType());
        connectorEntity.setDescription(connectorDTO.getDescription());
        connectorEntity.setConfig(JSON.toJSONString(connectorDTO.getConfig()));
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername()!=null){
            connectorEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
        return connectorEntity;
    }
}
