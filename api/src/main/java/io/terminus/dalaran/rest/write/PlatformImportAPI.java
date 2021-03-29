package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.ImportJarRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface PlatformImportAPI {

    @ApiOperation(value = "导入所有配置, 会覆盖之前的内容")
    @PostMapping(value = "/import")
    void importAll(@RequestParam MultipartFile importFile);

    @ApiOperation(value = "导入jar文件（测试）")
    @PostMapping(value = "/import/jar")
    void importJarFile(@RequestBody ImportJarRequest request);

    @ApiOperation(value = "本地开发资源上报")
    @PostMapping(value = "/resource/upload")
    BasicResponse localUpload(@RequestPart MultipartFile file, @RequestParam String name, @RequestParam String version, @RequestParam String resourceGroup);
}
