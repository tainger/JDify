package io.terminus.dalaran.model.flow;

public class FlowValidationBuilder {

    private FlowValidation validation;

    public static FlowValidationBuilder newBuilder() {
        return new FlowValidationBuilder();
    }

    public FlowValidationBuilder targetId(String targetId) {
        validation.setTargetId(targetId);
        return this;
    }

    public FlowValidationBuilder targetType(ValidateMessageTarget targetType) {
        validation.setTargetType(targetType);
        return this;
    }


    public FlowValidationBuilder trigger() {
        validation.setTargetType(ValidateMessageTarget.Trigger);
        return this;
    }

    public FlowValidationBuilder processor() {
        validation.setTargetType(ValidateMessageTarget.Processor);
        return this;
    }

    public FlowValidationBuilder flowEnd() {
        validation.setTargetType(ValidateMessageTarget.FlowEnd);
        return this;
    }

    public FlowValidationBuilder field(String field) {
        validation.setField(field);
        return this;
    }

    public FlowValidationBuilder message(FlowValidateMessage message) {
        validation.setMessage(message);
        return this;
    }

    public FlowValidationBuilder suggest(String suggest) {
        validation.setSuggest(suggest);
        return this;
    }

    public FlowValidation build() {
        return validation;
    }


}
