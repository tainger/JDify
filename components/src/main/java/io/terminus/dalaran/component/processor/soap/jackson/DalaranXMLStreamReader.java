package io.terminus.dalaran.component.processor.soap.jackson;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

/**
 * Created by jingdi on 2019/6/4
 */
public class DalaranXMLStreamReader extends StreamReaderDelegate {

    private String _rootElementLocalName;
    public DalaranXMLStreamReader (XMLStreamReader streamReader) {
        super(streamReader);
    }

    @Override
    public int next() throws XMLStreamException {
        int v = super.next();
        if (_rootElementLocalName == null) {
            _rootElementLocalName = super.getLocalName();
        }
        return v;
    }

    public String getLocalNameForRootElement() {
        return _rootElementLocalName;
    }
}
