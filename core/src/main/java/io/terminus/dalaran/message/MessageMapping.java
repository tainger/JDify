package io.terminus.dalaran.message;

import java.util.List;

/**
 * Created by jingdi on 2019/3/13
 */
public class MessageMapping {

    private DalaranMessage target;

    private DalaranMessage destination;

    private List<FieldMapping> mappings;

    private List<SingleFieldMapping> singleFieldMappings;

    public MessageMapping(DalaranMessage target, DalaranMessage destination, List<FieldMapping> mappings, List<SingleFieldMapping> singleFieldMappings) {
        this.target = target;
        this.destination = destination;
        this.mappings = mappings;
        this.singleFieldMappings = singleFieldMappings;
    }

    public DalaranMessage getTarget() {
        return target;
    }

    public void setTarget(DalaranMessage target) {
        this.target = target;
    }

    public DalaranMessage getDestination() {
        return destination;
    }

    public void setDestination(DalaranMessage destination) {
        this.destination = destination;
    }

    public List<FieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }

    public List<SingleFieldMapping> getSingleFieldMappings() {
        return singleFieldMappings;
    }

    public void setSingleFieldMappings(List<SingleFieldMapping> singleFieldMappings) {
        this.singleFieldMappings = singleFieldMappings;
    }
}
