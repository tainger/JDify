package io.terminus.dalaran.component.processor.soap;

import com.predic8.wsdl.Definitions;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import io.terminus.dalaran.core.model.converter.soap.model.SoapOperationConfig;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import java.io.StringWriter;

/**
 * Created by jingdi on 2019/5/23
 */
public class DalaranSoapProcessor implements Processor {

    private final SoapOperationConfig soapOperationConfig;

    private final Definitions definitions;

    public DalaranSoapProcessor(SoapOperationConfig soapOperationConfig, Definitions definitions) {
        this.soapOperationConfig = soapOperationConfig;
        this.definitions = definitions;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        StringWriter stringWriter = new StringWriter();
        Object body = exchange.getIn().getBody();

        RequestCreator requestCreator = new RequestCreator();
        MarkupBuilder markupBuilder = new MarkupBuilder(stringWriter);
        SOARequestCreator creator = new SOARequestCreator(definitions, requestCreator, markupBuilder);

        creator.setFormParams(body);
        creator.createRequest(soapOperationConfig.getPortType(), soapOperationConfig.getName(), soapOperationConfig.getBinding());

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(definitions.getBaseDir().toString());
        post.addHeader("Content-Type", "text/xml");
        post.setEntity(new StringEntity(stringWriter.toString()));

        CloseableHttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();

        exchange.getOut().setBody(EntityUtils.toString(entity));
    }
}
