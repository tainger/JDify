package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/api/repository/private", produces = {"application/json; charset=UTF-8"})
public interface PrivateRepositoryWriteAPI {

    @ApiOperation(value = "发布模板到市场")
    @PostMapping(value = "/publish")
    BasicResponse publish(@RequestBody BasicResourceDTO basicResource);

    @ApiOperation(value = "下载到私有仓库")
    @PostMapping(value = "/install")
    BasicResponse install(@RequestBody PrivateRepositoryDTO privateRepository);

    @ApiOperation(value = "本地开发资源上报")
    @PostMapping(value = "/local/upload")
    BasicResponse localUpload(@RequestPart MultipartFile file, @RequestParam String name, @RequestParam String version, @RequestParam String resourceGroup);
}
