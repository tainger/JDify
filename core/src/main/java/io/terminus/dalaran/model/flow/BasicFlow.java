package io.terminus.dalaran.model.flow;

import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public abstract class BasicFlow {

    @NotNull
    private Long id;

    private String version;

    private MessageModel inModel;

    private MessageModel outModel;

    @NotNull
    private List<ProcessorModel> pipeline;
}
