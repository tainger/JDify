package io.terminus.dalaran.console.entity;

import lombok.Data;
import org.apache.shardingsphere.elasticjob.tracing.event.JobExecutionEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "JOB_EXECUTION_LOG")
public class JobExecutionEntity {

    @Id
    private String id;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "hostname")
    private String hostname;

    @Column(name = "ip")
    private String ip;

    @Column(name = "sharding_item")
    private Integer shardingItem;

    @Column(name = "execution_source")
    private String executionSource;

    @Column(name = "failure_cause")
    private String failureCause;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "complete_time")
    private Date completeTime;

    /**
     * JobExecutionLog convert to JobExecutionEvent.
     *
     * @return JobExecutionEvent entity
     */
    public JobExecutionEvent toJobExecutionEvent() {
        return new JobExecutionEvent(
                id,
                hostname,
                ip,
                taskId,
                jobName,
                JobExecutionEvent.ExecutionSource.valueOf(executionSource),
                shardingItem,
                startTime,
                completeTime,
                isSuccess,
                failureCause
        );
    }

}

