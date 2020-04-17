package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.OSSManagementService;
import io.terminus.dalaran.rest.write.OSSWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class OSSManagementRest implements OSSWriteAPI {

    @Autowired
    private OSSManagementService ossManagementService;

    @Override
    @OnException(code = ResponseMessage.OSS_FILE_UPLOAD_ERROR)
    public String upload(MultipartFile file) {
        return ossManagementService.upload(file);
    }
}
