package io.terminus.dalaran.component.utils;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class DalaranFileUtils {

    public static String readFile(String localPath) throws Exception {
        Reader fileReader = new FileReader(localPath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        } finally {
            FileUtils.forceDeleteOnExit(new File(localPath));
            bufferedReader.close();
        }
    }

    public static File createFile(String fileKey) throws Exception {
        File dir = new File("/var/tmp");
        String fileName = "dalaran-" + fileKey.hashCode();
        return File.createTempFile(fileName, "", dir);
    }
}
