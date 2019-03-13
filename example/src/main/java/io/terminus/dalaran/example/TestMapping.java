package io.terminus.dalaran.example;

import io.terminus.dalaran.message.MessageMapping;
import io.terminus.dalaran.message.MessageMappingSet;
import io.terminus.dalaran.message.model.MessageModel;
import io.terminus.dalaran.message.model.ModelType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/3/13
 */
public class TestMapping {

    public static void main(String[] args) throws Exception {

        MessageModel model1 = new MessageModel();
        model1.setColumnName("name");
        model1.setColumnType("String");

        MessageModel model2 = new MessageModel();
        model2.setColumnName("number");
        model2.setColumnType("long");

        MessageModel model3 = new MessageModel();
        model3.setColumnName("userName");
        model3.setColumnType("String");

        MessageModel model4 = new MessageModel();
        model4.setColumnName("userNumber");
        model4.setColumnType("long");

        MessageMapping mapping1 = new MessageMapping();
        mapping1.setTargetModel(model1);
        mapping1.setDestinationModel(model3);

        MessageMapping mapping2 = new MessageMapping();
        mapping2.setTargetModel(model2);
        mapping2.setDestinationModel(model4);

        MessageMappingSet mappingSet = new MessageMappingSet();

        List<MessageMapping> mappings = new ArrayList<>();
        mappings.add(mapping1);
        mappings.add(mapping2);

        mappingSet.setMappings(mappings);
        mappingSet.setModelType(ModelType.JSON);


    }
}
