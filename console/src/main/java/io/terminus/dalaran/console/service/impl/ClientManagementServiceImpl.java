package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.ClientEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.ClientRepository;
import io.terminus.dalaran.console.service.ClientManagementService;
import io.terminus.dalaran.model.dto.ClientDTO;
import io.terminus.dalaran.model.dto.basic.BasicClientInfo;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Service
public class ClientManagementServiceImpl implements ClientManagementService {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Long create(ClientDTO clientDTO) {
        ClientEntity entity = toEntity(clientDTO);
        setCreatedBy(entity);
        repository.save(entity);
        return entity.getId();
    }

    @Override
    public ClientDTO update(ClientDTO clientDTO) {
        ClientEntity clientEntity = buildEntity(clientDTO);
        repository.save(clientEntity);
        return clientDTO;
    }

    @Override
    public void delete(Long appKey) {
        repository.deleteById(appKey);
    }

    @Override
    public ClientDTO detail(Long appKey) {
        ClientEntity entity = repository.findById(appKey).get();
        if (entity != null) {
            return toDTO(entity);
        }
        return null;
    }

    @Override
    public List<BasicClientInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicClientInfo> criteriaQuery = builder.createQuery(BasicClientInfo.class);
        Root<ClientEntity> root = criteriaQuery.from(ClientEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"))
                .where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private ClientDTO toDTO(ClientEntity entity) {
        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private ClientEntity toEntity(ClientDTO dto) {
        ClientEntity entity = new ClientEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private void setCreatedBy(ClientEntity clientEntity){
        if(UserContext.getUserInfo().getUsername()!=null){
            clientEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private ClientEntity buildEntity(ClientDTO clientDTO){
        ClientEntity clientEntity = repository.findById(clientDTO.getId()).get();
        clientEntity.setName(clientDTO.getName());
        clientEntity.setAppKey(clientDTO.getAppKey());
        clientEntity.setSecret(clientDTO.getSecret());
        clientEntity.setDescription(clientDTO.getDescription());
        if(UserContext.getUserInfo().getUsername()!=null){
            clientEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
        return clientEntity;
    }
}
