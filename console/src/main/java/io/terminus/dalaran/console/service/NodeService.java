package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.NodeDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NodeService {

    Page<NodeDTO> queryPageable(Integer pageNumber, Integer pageSize);

    List<NodeDTO> list();

    String create(NodeDTO nodeDTO);

    NodeDTO update(NodeDTO nodeDTO);

    void delete(String id);
}
