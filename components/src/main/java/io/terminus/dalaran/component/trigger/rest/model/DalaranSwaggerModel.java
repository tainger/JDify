package io.terminus.dalaran.component.trigger.rest.model;

import io.swagger.models.ExternalDocs;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import lombok.Data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class DalaranSwaggerModel implements Model {

    private Map<String, Property> properties;

    private List<String> required;

    private Object example;

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
    public Map<String, Property> getProperties() {
        return this.properties;
    }

    @Override
    public void setProperties(Map<String, Property> properties) {
        if (properties != null) {
            Iterator var2 = properties.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                this.addProperty(key, (Property)properties.get(key));
            }
        }
    }

    @Override
    public Object getExample() {
        return this.example;
    }

    @Override
    public void setExample(Object o) {
        this.example = o;
    }

    @Override
    public ExternalDocs getExternalDocs() {
        return null;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public void setReference(String s) {

    }

    @Override
    public Map<String, Object> getVendorExtensions() {
        return null;
    }

    @Override
    public Object clone() {
        return null;
    }

    public void addProperty(String key, Property property) {
        if (property != null) {
            if (this.properties == null) {
                this.properties = new LinkedHashMap();
            }

            if (this.required != null) {
                Iterator var3 = this.required.iterator();

                while(var3.hasNext()) {
                    String ek = (String)var3.next();
                    if (key.equals(ek)) {
                        property.setRequired(true);
                    }
                }
            }
            this.properties.put(key, property);
        }
    }
}
