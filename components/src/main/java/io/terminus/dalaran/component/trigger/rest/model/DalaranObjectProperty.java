package io.terminus.dalaran.component.trigger.rest.model;

import io.swagger.models.Xml;
import io.swagger.models.properties.Property;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class DalaranObjectProperty extends DalaranBasicProperty {

    private Map<String, Property> properties;

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public void setFormat(String format) {
        super.setFormat(format);
    }

    public DalaranObjectProperty(String type, String format) {
        super(type, format);
    }

    @Override
    public Property title(String s) {
        return super.title(s);
    }

    @Override
    public Property description(String s) {
        return super.description(s);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public String getFormat() {
        return super.getFormat();
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void setTitle(String s) {
        super.setTitle(s);
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public void setDescription(String s) {
        super.setDescription(s);
    }

    @Override
    public Boolean getAllowEmptyValue() {
        return super.getAllowEmptyValue();
    }

    @Override
    public void setAllowEmptyValue(Boolean aBoolean) {
        super.setAllowEmptyValue(aBoolean);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String s) {
        super.setName(s);
    }

    @Override
    public boolean getRequired() {
        return super.getRequired();
    }

    @Override
    public void setRequired(boolean b) {
        super.setRequired(b);
    }

    @Override
    public Object getExample() {
        return super.getExample();
    }

    @Override
    public void setExample(Object o) {
        super.setExample(o);
    }

    @Override
    public void setExample(String s) {
        super.setExample(s);
    }

    @Override
    public Boolean getReadOnly() {
        return super.getReadOnly();
    }

    @Override
    public void setReadOnly(Boolean aBoolean) {
        super.setReadOnly(aBoolean);
    }

    @Override
    public Integer getPosition() {
        return super.getPosition();
    }

    @Override
    public void setPosition(Integer integer) {
        super.setPosition(integer);
    }

    @Override
    public Xml getXml() {
        return super.getXml();
    }

    @Override
    public void setXml(Xml xml) {
        super.setXml(xml);
    }

    @Override
    public void setDefault(String s) {
        super.setDefault(s);
    }

    @Override
    public String getAccess() {
        return super.getAccess();
    }

    @Override
    public void setAccess(String s) {
        super.setAccess(s);
    }

    @Override
    public Map<String, Object> getVendorExtensions() {
        return super.getVendorExtensions();
    }

    @Override
    public Property rename(String s) {
        return super.rename(s);
    }

    public DalaranObjectProperty property(String name, Property property) {
        if (this.properties == null) {
            this.properties = new TreeMap();
        }
        this.properties.put(name, property);
        return this;
    }

}
