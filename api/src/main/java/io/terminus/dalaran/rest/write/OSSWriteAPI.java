package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/api/oss")
public interface OSSWriteAPI {

    @ApiOperation(value = "文件上传至oss")
    @PostMapping(value = "/file/upload")
    String upload(@RequestBody MultipartFile file);
}
