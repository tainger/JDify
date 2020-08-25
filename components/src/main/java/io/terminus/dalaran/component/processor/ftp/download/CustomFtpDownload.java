package io.terminus.dalaran.component.processor.ftp.download;

import io.terminus.dalaran.component.common.FtpProtocol;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "custom-ftp-download",
        order = 20,
        configType = CustomFtpDownloadConfig.class,
        bodyType = "CSV"
)
public class CustomFtpDownload implements DalaranProcessor<CustomFtpDownloadConfig> {

    private SFTPClient sftpClient;

    private SSHClient sshClient;

    @Override
    public void configure(ProcessorDefinition route, CustomFtpDownloadConfig config) {
        ftpConnectionConfig(config);
        route.process(new CustomFtpDownloadProcessor(sshClient, sftpClient, config));
    }

    private void ftpConnectionConfig(CustomFtpDownloadConfig config){
        try {
            if (sftpClient != null) {
                sftpClient.close();
            }
            if (sshClient != null && sshClient.isConnected()) {
                sshClient.close();
            }
            if (config.getConnector().getProtocol() == FtpProtocol.SFTP) {
                SSHClient sshClient = new SSHClient();
                sshClient.addHostKeyVerifier(new PromiscuousVerifier());
                sshClient.setConnectTimeout(config.getConnector().getTimeout().intValue());
                sshClient.connect(config.getConnector().getHost());
                sshClient.authPassword(config.getConnector().getUsername(), config.getConnector().getPassword());
                this.sftpClient = sshClient.newSFTPClient();
                this.sshClient = sshClient;
            }
        } catch (Exception e) {
            throw new RuntimeException("custom ftp download config error, " + e.getCause());
        }
    }
}
