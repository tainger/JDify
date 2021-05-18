package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.NodeDTO;
import org.springframework.data.domain.Page;

public interface NodeService {

    Page<NodeDTO> queryPageable(Integer pageNumber, Integer pageSize);

    String create(NodeDTO nodeDTO);

    NodeDTO update(NodeDTO nodeDTO);

    void delete(String resourceKey);
}
