package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/api/repository/private", produces = {"application/json; charset=UTF-8"})
public interface PrivateRepositoryWriteAPI {

    @ApiOperation(value = "发布模板到市场")
    @PostMapping(value = "/publish")
    BasicResponse publish(@RequestBody BasicResourceDTO basicResource);

    @ApiOperation(value = "下载到私有仓库")
    @PostMapping(value = "/install")
    BasicResponse install(@RequestBody PrivateRepositoryDTO privateRepository);

    @ApiOperation(value = "发布模板到市场")
    @PostMapping(value = "/local/upload")
    BasicResponse localUpload(MultipartFile file, @RequestBody BasicResourceDTO basicResource);
}
