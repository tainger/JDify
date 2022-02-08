package io.terminus.dalaran.component.ftp.processor.download;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.DALARAN_CONTEXT_EXCHANGE;
import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

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
            Map<String, Object> contextValues = (Map<String, Object>)exchange.getProperties().get(DALARAN_CONTEXT_EXCHANGE + exchange.getExchangeId());
            if (MapUtils.isNotEmpty(contextValues) && contextValues.containsKey(downloadConfig.getDynamicPath())) {
                fileName = String.valueOf(contextValues.get(downloadConfig.getDynamicPath()));
            } else {
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
            }
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
        if (downloadConfig.getEncrypted()) {
            return readEncryptedFile(localPath);
        }
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

    private String readEncryptedFile(String localPath) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(new File(localPath));
        byte[] buf = new byte[1024]; //数据中转站 临时缓冲区
        int length = 0;
        //循环读取文件内容，输入流中将最多buf.length个字节的数据读入一个buf数组中,返回类型是读取到的字节数。
        //当文件读取到结尾时返回 -1,循环结束。
        while ((length = fis.read(buf)) != -1) {
            byteArrayOutputStream.write(buf, 0, buf.length);
        }
        byte[] toByteArray = byteArrayOutputStream.toByteArray();
        return decrypt(toByteArray, downloadConfig.getDecryptKey(), downloadConfig.getDecryptIv());
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

    public static String decrypt(byte[] sSrc, String key, String iv) throws Exception {
        byte[] keyByte = DatatypeConverter.parseHexBinary(key);
        byte[] ivByte = DatatypeConverter.parseHexBinary(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivByte);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] doFinal = cipher.doFinal(sSrc);
        return IOUtils.toString(doFinal);
    }
}
