package io.terminus.dalaran.runtime.service;

import java.util.Date;

public interface TracingLogService {

    Long countElapseLog(Date oneMinBeforeCurrent, Date now, String flowId, Long elapse);

    Long countFailureLog(Date oneMinBeforeCurrent, Date now, String flowId);
}
