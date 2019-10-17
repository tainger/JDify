package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/sub-flow", produces = {"application/json; charset=UTF-8"})
public interface SubFlowWriteAPI {

    @ApiOperation(value = "创建子流程")
    @PostMapping(value = "/create")
    Long create(@RequestBody SubFlowDTO model);

    @ApiOperation(value = "更新子流程")
    @PostMapping(value = "/update")
    SubFlowDTO update(@RequestBody SubFlowDTO model);

    @ApiOperation(value = "删除子流程")
    @DeleteMapping(value = "/delete")
    void deleteById(@RequestParam Long id);

    @ApiOperation(value = "复制子流程")
    @PostMapping(value = "/copy")
    Long copy(@RequestBody CopyFlow copyFlow);
}
