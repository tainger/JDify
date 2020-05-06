package io.terminus.dalaran.component.processor.ftp.custom;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.terminus.dalaran.component.connector.FtpUploadConnector;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.resource.oss.OSSAccount;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@Processor(
        value = "custom-ftp-upload",
        order = 19,
        configType = CustomFtpUploadConfig.class,
        bodyType = "CSV"
)
public class CustomFtpUpload implements DalaranProcessor<CustomFtpUploadConfig> {

    private static final String FTP_ROUTE_URI = "%s:%s:%s/%s?passiveMode=true&fileExist=%s";

    private static final String DATA_TEMPLATE_DIR = "/var/tmp";

    @Autowired
    private OSSAccount ossAccount;

    @Override
    public void configure(ProcessorDefinition route, CustomFtpUploadConfig config) {
        FtpUploadConnector connector = config.getConnector();
        String uri = String.format(FTP_ROUTE_URI, connector.getProtocol().toString().toLowerCase(), connector.getHost(),
                connector.getPort(), config.getPath(), config.getFileExist());
        if (StringUtils.isNotBlank(connector.getUsername())) {
            uri += "&username=" + connector.getUsername();
        }
        if (StringUtils.isNotBlank(connector.getPassword())) {
            uri += "&password=" + connector.getPassword();
        }
        if (connector.getTimeout() != null) {
            uri += "&timeout=" + connector.getTimeout();
        }

        route.process(new CustomFTPUpLoadPreProcessor(config, templateConfigure(config))).to(uri).process(exchange -> {
            Object fileName = exchange.getIn().getHeader("CamelFileNameProduced");
            exchange.getOut().setBody(fileName);
        });
    }

    private Template templateConfigure(CustomFtpUploadConfig config) {
        Configuration configuration = new Configuration();
        File dir = new File(DATA_TEMPLATE_DIR);
        try {
            configuration.setDirectoryForTemplateLoading(dir);
            return configuration.getTemplate(getFileFromOss(config.getTemplateUri(), ossAccount, dir));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileFromOss(String fileKey, OSSAccount ossAccount, File dir) throws Exception {
        String fileName = "dalaran-" + fileKey.hashCode();
        File tempFile = File.createTempFile(fileName, ".ftl", dir);
        OSS ossClient = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        ossClient.getObject(new GetObjectRequest(ossAccount.getBucketName(), fileKey), tempFile);
        return tempFile.getName();
    }
}
