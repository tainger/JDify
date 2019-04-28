package io.terminus.dalaran.publish.entity;

import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Data
//@Entity
//@Table(name = "dalaran_publish_flow")
public class PublishFlowEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String version;

    private Long recordId;

    @NotNull
    private String triggerType;

    @Nullable
    private Object triggerConfig;

    @Nullable
    private Long name;

    private MessageModel inModel;

    private MessageModel outModel;
    @NotNull
    private Map<Long, ProcessorModel> processorMap;

    @NotNull
    private List<Long> processingPipeline;

}
