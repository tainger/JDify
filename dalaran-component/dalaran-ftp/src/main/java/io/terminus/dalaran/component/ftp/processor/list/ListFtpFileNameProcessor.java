package io.terminus.dalaran.component.ftp.processor.list;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

@Slf4j
public class ListFtpFileNameProcessor implements Processor {

    private SSHClient sshClient;

    private SFTPClient sftpClient;

    private ListFtpFileNameConfig listFtpFileNameConfig;

    public ListFtpFileNameProcessor(SSHClient sshClient, SFTPClient sftpClient, ListFtpFileNameConfig downloadConfig) {
        this.sshClient = sshClient;
        this.sftpClient = sftpClient;
        this.listFtpFileNameConfig = downloadConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        List<RemoteFileInfo> remoteFileInfoList = new ArrayList<>();
        try {
            List<RemoteResourceInfo> remoteResourceInfoList = sftpClient.ls(listFtpFileNameConfig.getPath());
            for (RemoteResourceInfo remoteResourceInfo : remoteResourceInfoList) {
                RemoteFileInfo remoteFileInfo = new RemoteFileInfo(remoteResourceInfo.getName(), remoteResourceInfo.getPath());
                remoteFileInfoList.add(remoteFileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sftpClient = reconnect();
        }
        exchange.getOut().setBody(remoteFileInfoList);
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
        sshClient.connect(listFtpFileNameConfig.getConnector().getHost());
        sshClient.authPassword(listFtpFileNameConfig.getConnector().getUsername(), listFtpFileNameConfig.getConnector().getPassword());
        this.sftpClient = sshClient.newSFTPClient();
        this.sshClient = sshClient;
        return this.sftpClient;
    }
}
