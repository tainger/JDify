package io.terminus.dalaran.component.http.trigger.model;

import io.swagger.models.Xml;
import io.swagger.models.properties.Property;
import lombok.Data;

import java.util.Map;

@Data
public class DalaranArrayProperty extends DalaranBasicProperty {

    private Property items;

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public void setFormat(String format) {
        super.setFormat(format);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    protected boolean canEqual(Object other) {
        return super.canEqual(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public DalaranArrayProperty(String type, String format) {
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
}
