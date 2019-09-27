package io.terminus.dalaran.component.processor;

import com.alibaba.fastjson.JSON;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.terminus.dalaran.component.trigger.soap.model.SoapApiInfo;
import io.terminus.dalaran.component.trigger.soap.model.SoapModel;
import io.terminus.dalaran.component.trigger.soap.utils.WSDLUtils;
import io.terminus.dalaran.model.schema.SoapSchema;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ServiceTest {

    @Test
    public void exportWSDL() {
        List<SoapApiInfo> list = new ArrayList<>();
//        SoapApiInfo apiInfo = new SoapApiInfo();

        SoapSchema schema1 = JSON.parseObject("{\"operationConfig\":{\"input\":\"MT_COMMON_REQ\",\"outPut\":\"MT_COMMON_RES\",\"portType\":\"SI_COMMON_S_OUT\",\"baseUrl\":\"piqas.shimaogroup.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_HYPERSMART&receiverParty=&receiverService=&interface=SI_COMMON_S_OUT&interfaceNamespace=urn%3A%3Ashimaogroup.com%3AI_HYPERSMART%3AECC\",\"protocol\":\"HTTP\",\"wsdl\":\"http://piqas.shimaogroup.com:50000/dir/wsdl?p=sa/2578ce33cd913812bbef5120fdee2c23\",\"name\":\"SI_COMMON_S_OUT\",\"binding\":\"SI_COMMON_S_OUTBinding\",\"modelRoot\":\"MT_COMMON_REQ\"},\"fields\":{\"root\":{\"nullable\":false,\"fields\":{\"MT_COMMON_REQ\":{\"type\":\"OBJECT\",\"fields\":{\"XMLDATA\":{\"nullable\":false,\"fields\":{},\"type\":\"STRING\"},\"TYPE\":{\"nullable\":false,\"fields\":{},\"type\":\"STRING\"}}}},\"type\":\"OBJECT\"}}}", SoapSchema.class);
        SoapModel model1 = new SoapModel("model-01", schema1);

        SoapSchema schema2 = JSON.parseObject("{\"operationConfig\":{\"input\":\"MT_COMMON_REQ\",\"outPut\":\"MT_COMMON_RES\",\"portType\":\"SI_COMMON_S_OUT\",\"baseUrl\":\"piqas.shimaogroup.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_HYPERSMART&receiverParty=&receiverService=&interface=SI_COMMON_S_OUT&interfaceNamespace=urn%3A%3Ashimaogroup.com%3AI_HYPERSMART%3AECC\",\"protocol\":\"HTTP\",\"wsdl\":\"http://piqas.shimaogroup.com:50000/dir/wsdl?p=sa/2578ce33cd913812bbef5120fdee2c23\",\"name\":\"SI_COMMON_S_OUT\",\"binding\":\"SI_COMMON_S_OUTBinding\",\"modelRoot\":\"MT_COMMON_REQ\"},\"fields\":{\"root\":{\"nullable\":false,\"fields\":{\"MT_COMMON_REQ\":{\"type\":\"OBJECT\",\"fields\":{\"XMLDATA\":{\"nullable\":false,\"fields\":{},\"type\":\"STRING\"},\"TYPE\":{\"nullable\":false,\"fields\":{},\"type\":\"STRING\"}}}},\"type\":\"OBJECT\"}}}", SoapSchema.class);
        SoapModel model2 = new SoapModel("model-02", schema2);

        SoapApiInfo apiInfo = new SoapApiInfo("soap-test", null, model1, model2);
        list.add(apiInfo);

        Definitions definitions =  WSDLUtils.buildDefinitions(list, "======");
        System.out.println(definitions.getAsString());
        Assert.assertNotNull(definitions);
    }

    @Test
    public void parseWSDL() {
        WSDLParser parser = new WSDLParser();
        Definitions definitions = parser.parse("https://soap-service-test.captain.terminus.io/ws/weather.wsdl");
        Definitions complex = parser.parse("http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx?wsdl");
        Assert.assertNotNull(definitions);
    }
}
