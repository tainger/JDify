package io.terminus.dalaran.component.processor.ftp.vfs.download;

import io.terminus.dalaran.component.connector.FtpUploadConnector;
import io.terminus.dalaran.component.utils.DalaranFileUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VFSFTPDownloadProcessor implements Processor {

    private VFSFTPDownloadConfig downloadConfig;

    public VFSFTPDownloadProcessor(VFSFTPDownloadConfig downloadConfig) {
        this.downloadConfig = downloadConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        FileSystemManager manager = VFS.getManager();
        String fileName = downloadConfig.getFileName();

        String localPath = "/var/tmp/" + fileName;
        FileObject local = manager.resolveFile(localPath);
        if (StringUtils.isNotBlank(downloadConfig.getDatePattern())) {
            fileName += downloadConfig.getDateConnector().getValue() + new SimpleDateFormat(downloadConfig.getDatePattern()).format(new Date());
        }
        if (StringUtils.isNotBlank(downloadConfig.getFileSuffix())) {
            fileName += downloadConfig.getFileSuffix();
        }
        String remotePath = downloadConfig.getPath() + "/" + fileName;

        FtpUploadConnector connector =  downloadConfig.getConnector();
        URI uri = new URI(connector.getProtocol().name().toLowerCase(), connector.getUsername() + ":" + connector.getPassword(), connector.getHost(), -1,
                remotePath, null, null);

        FileObject remote = manager.resolveFile(uri);
        local.copyFrom(remote, Selectors.SELECT_SELF);
        String fileContent = DalaranFileUtils.readFile(localPath);

        if (downloadConfig.getDelete()) {
            remote.delete();
        }
        exchange.getOut().setBody(fileContent);
        local.close();
        remote.close();
    }
}
