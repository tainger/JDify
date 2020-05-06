package io.terminus.dalaran.component.trigger.rest.model;

import io.swagger.models.Xml;
import io.swagger.models.properties.Property;
import lombok.Data;

import java.util.Map;

@Data
public class DalaranBasicProperty implements Property {

    private String type;

    private String format;

    public DalaranBasicProperty(String type, String format) {
        this.type = type;
        this.format = format;
    }

    @Override
    public Property title(String s) {
        return null;
    }

    @Override
    public Property description(String s) {
        return null;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getFormat() {
        return this.format;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle(String s) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String s) {

    }

    @Override
    public Boolean getAllowEmptyValue() {
        return null;
    }

    @Override
    public void setAllowEmptyValue(Boolean aBoolean) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public boolean getRequired() {
        return false;
    }

    @Override
    public void setRequired(boolean b) {

    }

    @Override
    public Object getExample() {
        return null;
    }

    @Override
    public void setExample(Object o) {

    }

    @Override
    public void setExample(String s) {

    }

    @Override
    public Boolean getReadOnly() {
        return null;
    }

    @Override
    public void setReadOnly(Boolean aBoolean) {

    }

    @Override
    public Integer getPosition() {
        return null;
    }

    @Override
    public void setPosition(Integer integer) {

    }

    @Override
    public Xml getXml() {
        return null;
    }

    @Override
    public void setXml(Xml xml) {

    }

    @Override
    public void setDefault(String s) {

    }

    @Override
    public String getAccess() {
        return null;
    }

    @Override
    public void setAccess(String s) {

    }

    @Override
    public Map<String, Object> getVendorExtensions() {
        return null;
    }

    @Override
    public Property rename(String s) {
        return null;
    }
}
