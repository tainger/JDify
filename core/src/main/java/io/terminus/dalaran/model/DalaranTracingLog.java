package io.terminus.dalaran.model;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.TracingType;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;


// TODO 应该跟 jpa 无关
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

    private String recordId;

    private String inputBody;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean testFlow;

    @Enumerated(EnumType.STRING)
    private TracingType tracingType;

    @Enumerated(EnumType.STRING)
    private BodyModelType inputBodyType;

    private String outputBody;

    @Enumerated(EnumType.STRING)
    private BodyModelType outputBodyType;
}