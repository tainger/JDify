package io.terminus.dalaran.component.processor;

import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.script.DalaranScript;
import io.terminus.dalaran.component.processor.script.DalaranScriptConfig;
import io.terminus.dalaran.component.processor.script.DalaranScriptType;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;

public class ScriptTest extends BasicProcessorTest {

    @Test
    public void testScriptComponent() {
        int INPUT_NUMBER = 9;
        DalaranScript processor = new DalaranScript();
        DalaranScriptConfig config = new DalaranScriptConfig();
        config.setType(DalaranScriptType.JavaScript);
        config.setScript("function execute(header, body) {return body * 2 + 3 - 4;}");

        ProducerTemplate template = getProcessorTemplate(processor, config);
        Assert.assertNotNull(template);
        Double result = (Double) template.requestBody(INPUT_NUMBER);
        Assert.assertEquals(result.compareTo((double) (INPUT_NUMBER * 2 + 3 - 4)), 0);
    }
}
