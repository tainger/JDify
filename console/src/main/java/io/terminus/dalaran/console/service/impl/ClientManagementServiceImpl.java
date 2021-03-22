package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.ClientEntity;
import io.terminus.dalaran.console.repository.ClientRepository;
import io.terminus.dalaran.console.service.ClientManagementService;
import io.terminus.dalaran.console.service.jpa.model.QueryClientInfo;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.model.dto.ClientDTO;
import io.terminus.dalaran.model.dto.basic.BasicClientInfo;
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
public class ClientManagementServiceImpl implements ClientManagementService {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String create(ClientDTO clientDTO) {
        ClientEntity entity = toEntity(clientDTO);
        setCreatedBy(entity);
        repository.save(entity);
        return entity.getResourceKey();
    }

    @Override
    public ClientDTO update(ClientDTO clientDTO) {
        ClientEntity clientEntity = buildEntity(clientDTO);
        repository.save(clientEntity);
        return clientDTO;
    }

    @Override
    public void delete(String appKey) {
        ClientEntity entity = repository.findByAppKey(appKey);
        entity.setExist(false);
        repository.save(entity);
    }

    @Override
    public ClientDTO detail(String appKey) {
        ClientEntity entity = repository.findByAppKey(appKey);
        if (entity != null) {
            return toDTO(entity);
        }
        return null;
    }

    @Override
    public List<BasicClientInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryClientInfo> criteriaQuery = builder.createQuery(QueryClientInfo.class);
        Root<ClientEntity> root = criteriaQuery.from(ClientEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("isExist"))
                .where(builder.equal(root.get("moduleId"), moduleId) , builder.equal(root.get("isExist"), true));
        List<QueryClientInfo> models = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicClientInfo> basicClients = new ArrayList<>();
        models.forEach(client -> {
            BasicClientInfo basicClient = new BasicClientInfo();
            try {
                BeanUtils.copyProperties(basicClient, client);
                basicClient.setId(client.getResourceKey());
                basicClients.add(basicClient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicClients;
    }

    private ClientDTO toDTO(ClientEntity entity) {
        ClientDTO dto = new ClientDTO();
        try {
            BeanUtils.copyProperties(entity, dto);
        }catch (Exception e) {
            e.printStackTrace();
        }
        dto.setId(entity.getResourceKey());
        return dto;
    }

    private ClientEntity toEntity(ClientDTO dto) {
        ClientEntity entity = new ClientEntity();
        try {
            BeanUtils.copyProperties(dto, entity);
        }catch (Exception e) {
            e.printStackTrace();
        }
        String resourceKey = dto.getId();
        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = GenerateKeyUtils.resourceKey();
        }
        entity.setResourceKey(resourceKey);
        entity.setExist(true);
        return entity;
    }

    private void setCreatedBy(ClientEntity clientEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            clientEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private ClientEntity buildEntity(ClientDTO clientDTO){
        ClientEntity clientEntity = repository.findByResourceKey(clientDTO.getId());
        clientEntity.setName(clientDTO.getName());
        clientEntity.setAppKey(clientDTO.getAppKey());
        clientEntity.setSecret(clientDTO.getSecret());
        clientEntity.setDescription(clientDTO.getDescription());
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null) {
            clientEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
        clientEntity.setExist(true);
        return clientEntity;
    }
}
