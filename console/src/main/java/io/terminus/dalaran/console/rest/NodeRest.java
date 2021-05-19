package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.service.NodeService;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.NodeDTO;
import io.terminus.dalaran.rest.read.NodeReadAPI;
import io.terminus.dalaran.rest.write.NodeWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NodeRest implements NodeReadAPI, NodeWriteAPI {

    @Autowired
    private NodeService nodeService;

    @Override
    public Page<NodeDTO> queryPageable(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return nodeService.queryPageable(pageNumber, pageSize);
    }

    @Override
    public List<NodeDTO> list() {
        return nodeService.list();
    }

    @Override
    public CreateResponse create(@RequestBody NodeDTO nodeDTO) {
        return new CreateResponse(nodeService.create(nodeDTO));
    }

    @Override
    public NodeDTO update(@RequestBody NodeDTO nodeDTO) {
        return nodeService.update(nodeDTO);
    }

    @Override
    public void delete(@RequestParam String resourceKey) {
        nodeService.delete(resourceKey);
    }
}
