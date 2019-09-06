package io.terminus.dalaran.console.log;

import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Calendar;

public class DeleteLogRetentionPolicy implements LogRetentionPolicy {

    @Autowired
    private TracingLogRepository repository;

    private final Integer duration;

    public DeleteLogRetentionPolicy(Integer duration) {
        this.duration = duration;
    }

    @Override
    @Transactional
    public void processing() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, -duration);
        repository.deleteByCreatedAtBefore(now.getTime());
    }
}
