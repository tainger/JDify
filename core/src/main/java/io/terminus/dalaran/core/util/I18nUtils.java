package io.terminus.dalaran.core.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18nUtils {

    @Autowired
    private MessageSource messageSource;

    private static final String EXCEPTION_PREFIX = "exception.";
    private static final String COMPONENT_PREFIX = "component.";


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

    public String getComponentMessage(@NotNull String code) {
        return getComponentMessage(code, null);
    }

    public String getComponentMessage(@NotNull String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getComponentMessage(code, args, locale);
    }

    public String getComponentMessage(@NotNull String code, Object[] args, Locale locale) {
        return getMessage(COMPONENT_PREFIX + code, args, locale);
    }

    public String getMessage(@NotNull String code) {
        return getMessage(code, null);
    }

    public String getMessage(@NotNull String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }

    public String getMessage(@NotNull String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }


}
