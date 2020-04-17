package io.terminus.dalaran.console.service;

import org.springframework.web.multipart.MultipartFile;

public interface OSSManagementService {

    String upload(MultipartFile file);
}
