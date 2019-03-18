package io.terminus.dalaran.utils;

import io.terminus.dalaran.message.FieldMapping;
import io.terminus.dalaran.message.MessageMapping;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/15
 */
public class XmlUtils {

    public static String createFile(MessageMapping messageMapping, Class c) throws Exception {
        Document document = DocumentHelper.createDocument();

        Element root = document.addElement("mappings", "http://dozermapper.github.io/schema/bean-mapping");
        root.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance")
                , "http://dozermapper.github.io/schema/bean-mapping http://dozermapper.github.io/schema/bean-mapping.xsd");
        Element mapping = root.addElement("mapping");
        mapping.addElement("class-a").addText("org.apache.camel.component.dozer.ExpressionMapper");
        mapping.addElement("class-b").addText("XXXX");

        List<FieldMapping> mappings = messageMapping.getMappings();
        for (FieldMapping fieldMapping : mappings) {
            for (Map.Entry<List<String>, List<String>> entry : fieldMapping.getMapping().entrySet()) {
                String targetField = entry.getKey() + "";
                String destinationField = entry.getValue() + "";
                Element field = mapping.addElement("field");
                field.addAttribute("custom-converter-id", "_expressionMapping")
                        .addAttribute("custom-converter-param", "simple:\\${body." + targetField + "}");
                field.addElement("a").addText("expression");
                field.addElement("b").addText(destinationField);
            }
        }
        String filePath = "/test.xml";
        System.out.println(filePath);
        File file = new File(filePath);
        file.createNewFile();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileWriter(file), format);
        writer.write(document);
        writer.close();

        return filePath;

    }

}
