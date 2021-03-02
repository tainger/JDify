package io.terminus.dalaran.runtime.service;

import java.util.Date;

public interface TracingLogService {

    Long countElapseLog(Date oneMinBeforeCurrent, Date now, Long flowId, Long elapse);

    Long countFailureLog(Date oneMinBeforeCurrent, Date now, Long flowId);
}
