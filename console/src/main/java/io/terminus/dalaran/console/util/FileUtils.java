package io.terminus.dalaran.console.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File transfer(MultipartFile origin) throws IOException {
        File dir = new File("/var/tmp");
        String fileName = "dalaran-" + origin.getOriginalFilename();
        File dest = File.createTempFile(fileName, "", dir);
        origin.transferTo(dest);
        return dest;
    }
}
