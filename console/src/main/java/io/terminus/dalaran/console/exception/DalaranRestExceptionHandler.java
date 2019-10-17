package io.terminus.dalaran.console.exception;

import io.terminus.dalaran.exception.DalaranRestException;
import io.terminus.dalaran.exception.DalaranRuntimeException;
import io.terminus.dalaran.model.common.ErrorMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class DalaranRestExceptionHandler {

    @ResponseBody
    @ExceptionHandler
    public ErrorMessage handleConflict(Exception ex, HttpServletResponse response, HandlerMethod method) {
        ErrorMessage message = new ErrorMessage();
        if (ex instanceof DalaranRestException || ex instanceof DalaranRuntimeException) {
            response.setStatus(599);
            message.setExceptionType(ex.getClass().getCanonicalName());
            message.setMessage(ex.getMessage());
        } else {
            OnException exceptionMessage = method.getMethodAnnotation(OnException.class);
            if (exceptionMessage != null) {
                response.setStatus(599);
                message.setExceptionType(exceptionMessage.exception().getCanonicalName());
                message.setMessage(exceptionMessage.message());
            } else {
                message.setMessage(ex.getMessage());
            }
        }
        return message;
    }
}
