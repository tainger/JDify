package io.terminus.dalaran.component.processor.ftp.vfs.download;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "VFS-FTP-Download",
        order = 22,
        configType = VFSFTPDownloadConfig.class,
        bodyType = "CSV"
)
public class VFSFTPDownload implements DalaranProcessor<VFSFTPDownloadConfig> {

    @Override
    public void configure(ProcessorDefinition route, VFSFTPDownloadConfig config) {
        route.process(new VFSFTPDownloadProcessor(config));
    }
}
