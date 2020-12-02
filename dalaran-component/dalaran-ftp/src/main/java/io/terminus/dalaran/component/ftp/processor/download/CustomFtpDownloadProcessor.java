package io.terminus.dalaran.component.ftp.processor.download;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CustomFtpDownloadProcessor implements Processor {

    private SSHClient sshClient;

    private SFTPClient sftpClient;

    private CustomFtpDownloadConfig downloadConfig;

    public CustomFtpDownloadProcessor(SSHClient sshClient, SFTPClient sftpClient, CustomFtpDownloadConfig downloadConfig) {
        this.sshClient = sshClient;
        this.sftpClient = sftpClient;
        this.downloadConfig = downloadConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (sftpClient == null) {
            log.warn("sftpClient is null! Please check custom ftp download config");
            return;
        }

        String fileName;
        if (downloadConfig.isDynamicFileName()) {
            Object in = exchange.getIn().getBody();
            JSON body;
            if (in instanceof String) {
                body = JSON.parseObject((String)in, JSON.class);
            } else if (in instanceof byte[]) {
                body = JSON.parseObject(IOUtils.toString((byte[])in), JSON.class);
            } else {
                body = JSON.parseObject(JSON.toJSONString(in), JSON.class);
            }
            if (!JSONPath.contains(body, downloadConfig.getDynamicPath())) {
                throw new RuntimeException("body: " + body + ", no file name");
            }
            fileName = JSONPath.eval(body, downloadConfig.getDynamicPath()).toString();
        } else {
            fileName = downloadConfig.getFileName();
            if (StringUtils.isNotBlank(downloadConfig.getDatePattern())) {
                fileName += downloadConfig.getDateConnector().getValue() + new SimpleDateFormat(downloadConfig.getDatePattern()).format(new Date());
            }
            if (StringUtils.isNotBlank(downloadConfig.getFileSuffix())) {
                fileName += downloadConfig.getFileSuffix();
            }
        }
        String remotePath = downloadConfig.getPath() + "/" + fileName;
        String localPath = "/var/tmp/" + fileName;
        try {
            sftpClient.get(remotePath, localPath);
        } catch (Exception e) {
            e.printStackTrace();
            sftpClient = reconnect();
            sftpClient.get(remotePath, localPath);
        }
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

    private SFTPClient reconnect() throws Exception {
        if (sftpClient != null) {
            sftpClient.close();
        }
        if (sshClient != null) {
            sshClient.close();
        }
        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.setConnectTimeout(30000);
        sshClient.connect(downloadConfig.getConnector().getHost());
        sshClient.authPassword(downloadConfig.getConnector().getUsername(), downloadConfig.getConnector().getPassword());
        this.sftpClient = sshClient.newSFTPClient();
        this.sshClient = sshClient;
        return this.sftpClient;
    }
}
