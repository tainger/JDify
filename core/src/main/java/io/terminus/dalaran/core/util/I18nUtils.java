package io.terminus.dalaran.core.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class I18nUtils {

    @Autowired
    private MessageSource messageSource;

    private static final String EXCEPTION_PREFIX = "exception.";
    private static final String COMPONENT_PREFIX = "component.";
    private static final String COMPONENT_FIELD_PREFIX = "component_field.";
    private static final String CONNECTOR_FIELD_PREFIX = "connector_field.";
    private static final String LIMITER_FIELD_PREFIX = "limiter_field.";
    private static final String MODEL_PREFIX = "model.";
    private static final String MODEL_FIELD_PREFIX = "model_field.";

    public String getExceptionMessage(@NotNull String code) {
        return getExceptionMessage(code, null);
    }

    public String getExceptionMessage(@NotNull String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getExceptionMessage(code, args, locale);
    }

    public String getExceptionMessage(@NotNull String code, Object[] args, Locale locale) {
        return getMessage(EXCEPTION_PREFIX + code, args, locale);
    }

    public String getMessage(@NotNull String code) {
        return getMessage(code, null);
    }

    public String getMessage(@NotNull String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(code, args, locale);
    }

    public String getMessage(@NotNull String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }

    public Optional<String> getMessageOptional(@NotNull String code) {
        return getMessageOptional(code, null);
    }

    public Optional<String> getMessageOptional(@NotNull String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessageOptional(code, args, locale);
    }

    public Optional<String> getMessageOptional(@NotNull String code, Object[] args, Locale locale) {
        try {
            return Optional.of(messageSource.getMessage(code, args, locale));
        } catch (NoSuchMessageException e) {
            return Optional.empty();
        }
    }

    public String getComponentName(@NotNull String componentType) {
        return getMessageOptional(COMPONENT_PREFIX + componentType).orElse(componentType);
    }

    public String getConnectorFieldLabel(String connector, String field) {
        Optional<String> message = getMessageOptional(CONNECTOR_FIELD_PREFIX + connector + "." + field);
        return message.orElse(field);
    }

    public String getLimiterFieldLabel(String limiter, String field) {
        Optional<String> message = getMessageOptional(LIMITER_FIELD_PREFIX + limiter + "." + field);
        return message.orElse(field);
    }

    public String getComponentFieldLabel(String component, String field) {
        Optional<String> message = getMessageOptional(COMPONENT_FIELD_PREFIX + component + "." + field);
        return message.orElse(field);
    }

    public String getModelName(String modelType) {
        return getMessageOptional(MODEL_PREFIX + modelType).orElse(modelType);
    }

    public String getModelFieldLabel(String model, String field) {
        Optional<String> message = getMessageOptional(MODEL_FIELD_PREFIX + model + "." + field);
        return message.orElse(field);
    }
}
