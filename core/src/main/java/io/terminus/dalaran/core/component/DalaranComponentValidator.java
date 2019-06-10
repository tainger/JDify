package io.terminus.dalaran.core.component;

import io.terminus.dalaran.core.flow.model.FlowValidation;

import java.util.List;

public interface DalaranComponentValidator<T> {

    List<FlowValidation> validate(T config);
}
