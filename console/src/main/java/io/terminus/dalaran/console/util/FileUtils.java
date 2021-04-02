package io.terminus.dalaran.console.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File transfer(MultipartFile origin) throws IOException {
        File dir = new File("/var/tmp");
        String fileName = "dalaran-" + origin.getOriginalFilename().hashCode();
        File dest = File.createTempFile(fileName, ".jar", dir);
        origin.transferTo(dest);
        return dest;
    }
}
