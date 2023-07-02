package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.NodeEntity;
import io.terminus.dalaran.console.model.UserContext;
import io.terminus.dalaran.console.repository.NodeRepository;
import io.terminus.dalaran.console.service.NodeService;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.model.dto.NodeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    @Override
    public Page<NodeDTO> queryPageable(Integer pageNumber, Integer pageSize) {
        Sort order = new Sort(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, order);
        Page<NodeEntity> page = nodeRepository.findAll(buildSpecification(), pageable);
        return new PageImpl<>(page.stream().map(this::buildNodeDTO).collect(Collectors.toList()), pageable, page.getTotalElements());
    }

    @Override
    public List<NodeDTO> list() {
        List<NodeEntity> nodeEntityList = nodeRepository.findByIsExistTrue();
        List<NodeDTO> nodeDTOList = new LinkedList<>();
        for (NodeEntity nodeEntity : nodeEntityList) {
            nodeDTOList.add(buildNodeDTO(nodeEntity));
        }
        return nodeDTOList;
    }

    @Override
    public String create(NodeDTO nodeDTO) {
        NodeEntity nodeEntity = new NodeEntity();
        buildEntity(nodeDTO, nodeEntity);
        setCreatedBy(nodeEntity);
        return nodeRepository.save(nodeEntity).getResourceKey();
    }

    @Override
    public NodeDTO update(NodeDTO nodeDTO) {
        NodeEntity nodeEntity = nodeRepository.findByResourceKey(nodeDTO.getId());
        buildEntity(nodeDTO, nodeEntity);
        nodeRepository.save(nodeEntity);
        return nodeDTO;
    }

    @Override
    public void delete(String id) {
        NodeEntity nodeEntity = nodeRepository.findByResourceKey(id);
        nodeEntity.setExist(false);
        nodeRepository.save(nodeEntity);
    }

    private void buildEntity(NodeDTO nodeDTO, NodeEntity nodeEntity){
        nodeEntity.setName(nodeDTO.getName());
        nodeEntity.setCompany(nodeDTO.getCompany());
        nodeEntity.setApplication(nodeDTO.getApplication());
        nodeEntity.setSystems(nodeDTO.getSystem());
        nodeEntity.setIcon(nodeDTO.getIcon());
        nodeEntity.setIconColour(nodeDTO.getIconColour());
        String resourceKey = nodeDTO.getId();
        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = GenerateKeyUtils.resourceKey();
        }
        nodeEntity.setResourceKey(resourceKey);
        nodeEntity.setExist(true);
    }

    private Specification<NodeEntity> buildSpecification() {
        return (root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("isExist"), true));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private NodeDTO buildNodeDTO(NodeEntity entity) {
        NodeDTO nodeDTO = new NodeDTO();
        nodeDTO.setName(entity.getName());
        nodeDTO.setId(entity.getResourceKey());
        nodeDTO.setCompany(entity.getCompany());
        nodeDTO.setApplication(entity.getApplication());
        nodeDTO.setSystem(entity.getSystems());
        nodeDTO.setIcon(entity.getIcon());
        nodeDTO.setIconColour(entity.getIconColour());
        return nodeDTO;
    }

    private void setCreatedBy(NodeEntity entity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            entity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

}
