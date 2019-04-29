package io.terminus.dalaran.console.service.impl;

import com.google.gson.Gson;
import io.terminus.dalaran.console.model.dto.BasicConnectorInfo;
import io.terminus.dalaran.console.model.dto.ConnectorDTO;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.entity.ConnectorEntity;
import io.terminus.dalaran.repository.ConnectorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private EntityManager entityManager;

    private final Gson gson = new Gson();

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
        connectorRepository.delete(connectorId);
    }

    @Override
    public ConnectorDTO detail(Long connectorId) {
        ConnectorEntity entity = connectorRepository.findOne(connectorId);
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
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name")).where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private ConnectorDTO toDTO(ConnectorEntity entity) {
        ConnectorDTO dto = new ConnectorDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setModuleId(entity.getModuleId());
        dto.setConfig(gson.fromJson(entity.getConfig(), Map.class));
        return dto;
    }

    private ConnectorEntity toEntity(ConnectorDTO dto) {
        ConnectorEntity entity = new ConnectorEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setModuleId(dto.getModuleId());
        entity.setConfig(gson.toJson(dto.getConfig()));
        return entity;
    }

}
