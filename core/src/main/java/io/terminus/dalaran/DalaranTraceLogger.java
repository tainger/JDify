package io.terminus.dalaran;

import io.terminus.dalaran.model.DalaranTracingLog;

public interface DalaranTraceLogger {

    void log(DalaranTracingLog tracingInfo);

}
