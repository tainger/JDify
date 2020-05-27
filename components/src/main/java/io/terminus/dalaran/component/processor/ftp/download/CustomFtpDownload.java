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

    @Override
    public void configure(ProcessorDefinition route, CustomFtpDownloadConfig config) {
        ftpConnectionConfig(config);
        route.process(new CustomFtpDownloadProcessor(sftpClient, config));
    }

    private void ftpConnectionConfig(CustomFtpDownloadConfig config){
        try {
            if (config.getConnector().getProtocol() == FtpProtocol.SFTP) {
                SSHClient client = new SSHClient();
                client.addHostKeyVerifier(new PromiscuousVerifier());
                client.connect(config.getConnector().getHost());
                client.authPassword(config.getConnector().getUsername(), config.getConnector().getPassword());
                this.sftpClient = client.newSFTPClient();
            }
        } catch (Exception e) {
            throw new RuntimeException("custom ftp download config error, " + e.getCause());
        }
    }
}
