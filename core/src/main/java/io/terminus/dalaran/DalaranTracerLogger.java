package io.terminus.dalaran;

import io.terminus.dalaran.model.DalaranTracingInfo;

public interface DalaranTracerLogger {

    void log(DalaranTracingInfo tracingInfo);

}
