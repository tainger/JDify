package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.DalaranModelTemplate;
import io.terminus.dalaran.model.dto.ModelDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/model", produces = {"application/json; charset=UTF-8"})
public interface ModelWriteAPI {

    @ApiOperation(value = "创建数据模型")
    @PostMapping(value = "/create")
    Long create(@RequestBody ModelDTO model);

    @ApiOperation(value = "更新数据模型")
    @PostMapping(value = "/update")
    ModelDTO update(@RequestBody ModelDTO model);

    @ApiOperation(value = "删除数据模型")
    @DeleteMapping(value = "/delete")
    void deleteById(@RequestParam Long id);

    @ApiOperation(value = "根据模型结构生成数据样例")
    @PostMapping(value = "/{id}/build/data-template")
    DalaranModelTemplate buildRequestTemplate(@RequestBody DalaranModelSchema schema, @PathVariable long id);
}
