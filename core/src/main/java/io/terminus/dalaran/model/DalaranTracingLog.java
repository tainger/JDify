package io.terminus.dalaran.model;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.support.trace.TracingType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dalaran_tracing_log")
public class DalaranTracingLog {

    @Id
    @GeneratedValue
    private Long id;

    private Long triggerId;

    private Long flowId;

    private Long processorId;

    private Long timestamp;

    private Long elapsed;

    private String inputBody;

    @Enumerated(EnumType.STRING)
    private TracingType tracingType;

    @Enumerated(EnumType.STRING)
    private BodyModelType inputBodyType;

    private String outputBody;

    @Enumerated(EnumType.STRING)
    private BodyModelType outputBodyType;
}