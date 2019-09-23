package io.terminus.dalaran.core.flow;

import org.apache.camel.AsyncProcessor;
import org.apache.camel.Processor;
import org.apache.camel.Service;
import org.apache.camel.Traceable;
import org.apache.camel.model.NoOutputDefinition;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.apache.camel.processor.DelegateSyncProcessor;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.util.ObjectHelper;

import javax.xml.bind.annotation.*;

@Metadata(label = "eip,endpoint")
@XmlRootElement(name = "dalaran-process")
@XmlAccessorType(XmlAccessType.FIELD)
public class DalaranProcessDefinition extends NoOutputDefinition<ProcessDefinition> {
    @XmlAttribute(required = true)
    private String ref;
    @XmlTransient
    private Processor processor;

    public DalaranProcessDefinition() {
    }

    public DalaranProcessDefinition(Processor processor) {
        this.processor = processor;
    }

    @Override
    public String toString() {
        if (processor instanceof Traceable) {
            return ((Traceable) processor).getTraceLabel();
        } else {
            if (ref != null) {
                return "process[ref:" + ref + "]";
            } else {
                return processor.toString();
            }
        }
    }

    @Override
    public String getShortName() {
        return toString();
    }

    @Override
    public String getLabel() {
        return toString();
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public Processor createProcessor(RouteContext routeContext) {
        Processor answer = processor;
        if (processor == null) {
            ObjectHelper.notNull(ref, "ref", this);
            answer = routeContext.mandatoryLookup(getRef(), Processor.class);
        }

        // ensure its wrapped in a Service so we can manage it from eg. JMX
        // (a Processor must be a Service to be enlisted in JMX)
        if (!(answer instanceof Service)) {
            if (answer instanceof AsyncProcessor) {
                // the processor is async by nature so use the async delegate
                answer = new DelegateAsyncProcessor(answer);
            } else {
                // the processor is sync by nature so use the sync delegate
                answer = new DelegateSyncProcessor(answer);
            }
        }
        return answer;
    }
}
