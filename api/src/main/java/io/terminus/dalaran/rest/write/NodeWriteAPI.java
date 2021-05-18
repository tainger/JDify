package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.NodeDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/node", produces = {"application/json; charset=UTF-8"})
public interface NodeWriteAPI {

    @ApiOperation(value = "创建节点")
    @PostMapping(value = "/create")
    CreateResponse create(@RequestBody NodeDTO nodeDTO);

    @ApiOperation(value = "更新节点")
    @PostMapping(value = "/update")
    NodeDTO update(@RequestBody NodeDTO nodeDTO);

    @ApiOperation(value = "删除节点")
    @DeleteMapping(value = "/delete")
    void delete(@RequestParam String resourceKey);

}
