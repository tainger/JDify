package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ImportJarRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface PlatformImportAPI {

    @ApiOperation(value = "导入所有配置, 会覆盖之前的内容")
    @PostMapping(value = "/import")
    void importAll(@RequestParam MultipartFile importFile);

    @ApiOperation(value = "导入jar文件（测试）")
    @PostMapping(value = "/import/jar")
    void importJarFile(@RequestBody ImportJarRequest request);
}
