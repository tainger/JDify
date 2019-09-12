package io.terminus.dalaran.component.trigger.soap.utils;

import org.apache.xalan.xsltc.runtime.ErrorMessages_zh_CN;
import org.xml.sax.InputSource;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WSUtils {

    public static void main(String[] args) {
        try {
            buildWS();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void buildWS() throws Exception {
        InputStream in_withcode = new ByteArrayInputStream("http://piqas.shimaogroup.com:50000/dir/wsdl?p=sa/2578ce33cd913812bbef5120fdee2c23".getBytes("UTF-8"));
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader reader = wsdlFactory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", true);
        reader.setFeature("javax.wsdl.importDocuments", true);
        Definition def = reader.readWSDL("", new InputSource(in_withcode));
        System.out.println(def);
    }
}
