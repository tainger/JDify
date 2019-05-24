package io.terminus.dalaran.component.processor.soap;

import com.alibaba.fastjson.JSON;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import io.terminus.dalaran.component.processor.soap.model.SoapProcessorConfig;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

/**
 * Created by jingdi on 2019/5/23
 */
public class DalaranSoapProcessor implements Processor {

    private final SoapProcessorConfig processorConfig;

    public DalaranSoapProcessor(SoapProcessorConfig processorConfig) {
        this.processorConfig = processorConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        WSDLParser parser = new WSDLParser();
        Definitions definitions = parser.parse(processorConfig.getWsdl());

        StringWriter stringWriter = new StringWriter();
        String body = exchange.getIn().getBody(String.class);
        Map data = JSON.parseObject(body, Map.class);

        Map formParams = Collections.singletonMap("xpath:/getCountryRequest/name", data.get("name"));

        RequestCreator requestCreator = new RequestCreator();
        MarkupBuilder markupBuilder = new MarkupBuilder(stringWriter);
        SOARequestCreator creator = new SOARequestCreator(definitions, requestCreator, markupBuilder);

        creator.setFormParams(formParams);
        creator.createRequest(processorConfig.getPortTypeName(), processorConfig.getOperationName(), processorConfig.getBindingName());

        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost(definitions.getBaseDir().toString());
        post.addHeader("Content-Type", "text/xml");
        post.setEntity(new StringEntity(stringWriter.toString()));

        CloseableHttpResponse response = client.execute(post);
        exchange.getOut().setBody(response.getEntity().getContent());
    }
}
