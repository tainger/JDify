package io.terminus.dalaran.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.terminus.dalaran.exception.DalaranException;
import io.terminus.dalaran.model.common.ErrorMessage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class DalaranCustomErrorDecoder implements ErrorDecoder {

    private ErrorDecoder errorDecoder = new Default();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 599) {
            try {
                InputStream respBody = response.body().asInputStream();
                ErrorMessage errorMessage = objectMapper.readValue(respBody, ErrorMessage.class);
                if (StringUtils.isNotBlank(errorMessage.getExceptionType())) {
                    Class<? extends DalaranException> exceptionClass = (Class<? extends DalaranException>) Class.forName(errorMessage.getExceptionType());
                    DalaranException exception = exceptionClass.getConstructor(ErrorMessage.class).newInstance(errorMessage);
                    return exception;
                }
            } catch (ClassNotFoundException | IOException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return errorDecoder.decode(methodKey, response);
    }
}
