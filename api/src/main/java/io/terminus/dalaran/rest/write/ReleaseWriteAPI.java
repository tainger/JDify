package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.model.dto.ReleaseRequestDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface ReleaseWriteAPI {

    @ApiOperation(value = "发布最新的所有内容")
    @PostMapping(value = "/release")
    ReleaseRecordDTO release(@RequestBody ReleaseRequestDTO requestDTO);

    @ApiOperation(value = "回滚至具体版本")
    @PostMapping(value = "/release/{version:.*}/rollback")
    ReleaseRecordDTO rollback(@PathVariable String version);
}
