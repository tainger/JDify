package io.terminus.dalaran.console.exception;

import io.terminus.dalaran.core.util.I18nUtils;
import io.terminus.dalaran.exception.DalaranRestException;
import io.terminus.dalaran.exception.DalaranRuntimeException;
import io.terminus.dalaran.exception.DalaranThrowable;
import io.terminus.dalaran.model.common.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class DalaranRestExceptionHandler {

    @Autowired
    private I18nUtils i18nUtils;

    @ResponseBody
    @ExceptionHandler
    public ErrorMessage handleConflict(Exception ex, HttpServletResponse response, HandlerMethod method) {
        ex.printStackTrace();
        ErrorMessage message = new ErrorMessage();
        if (ex instanceof DalaranRestException || ex instanceof DalaranRuntimeException) {
            response.setStatus(599);
            message.setExceptionType(ex.getClass().getCanonicalName());
            message.setCode(((DalaranThrowable) ex).getCode());
            message.setLocalMessage(i18nUtils.getExceptionMessage(message.getCode()));
            message.setExceptionMessage(ex.getMessage());
        } else {
            OnException exceptionMessage = method.getMethodAnnotation(OnException.class);
            if (exceptionMessage != null) {
                response.setStatus(599);
                message.setExceptionType(exceptionMessage.exception().getCanonicalName());
                message.setCode(exceptionMessage.code());
                message.setLocalMessage(i18nUtils.getExceptionMessage(exceptionMessage.code()));
                message.setExceptionMessage(exceptionMessage.exceptionMessage());
            } else {
                message.setLocalMessage(ex.getMessage());
                message.setExceptionMessage(ex.getMessage());
            }
        }
        return message;
    }
}
