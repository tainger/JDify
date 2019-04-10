package io.terminus.dalaran.processor.mapper.jxpath;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/20
 */
public class DalaranJXPathFactory extends AbstractFactory {
    @Override
    public boolean createObject(JXPathContext context, Pointer pointer,
                                Object parent, String name, int index){
        if(parent instanceof Map) {
            Map child = new HashMap();
            ((Map<String, Object>)parent).put(name, child);
        }
        return true;
    }
}
