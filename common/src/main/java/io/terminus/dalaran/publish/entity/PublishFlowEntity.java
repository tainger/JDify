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

    private Long recordId;

    @Nullable
    private Long name;

    private MessageModel inModel;

    private MessageModel outModel;

    @NotNull
    private List<ProcessorModel> processors;

    @NotNull
    private Map<String, String> properties;

}
