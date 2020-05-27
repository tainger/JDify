package io.terminus.dalaran.component.processor.ftp.download;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

@Slf4j
public class CustomFtpDownloadProcessor implements Processor {

    private SFTPClient sftpClient;

    private CustomFtpDownloadConfig downloadConfig;

    public CustomFtpDownloadProcessor(SFTPClient sftpClient, CustomFtpDownloadConfig downloadConfig) {
        this.sftpClient = sftpClient;
        this.downloadConfig = downloadConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (sftpClient == null) {
            log.warn("sftpClient is null! Please check custom ftp download config");
            return;
        }
        String remotePath = downloadConfig.getPath() + "/" + downloadConfig.getFileName();
        String localPath = "/var/tmp/" + downloadConfig.getFileName();
        sftpClient.get(remotePath, localPath);
        String fileContent = readFile(localPath);
        if (downloadConfig.getDelete()) {
            sftpClient.rm(remotePath);
        }
        exchange.getOut().setBody(fileContent);
    }

    private String readFile(String localPath) throws Exception {
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
}
