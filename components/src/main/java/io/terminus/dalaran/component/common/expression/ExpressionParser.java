package io.terminus.dalaran.component.common.expression;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import io.terminus.dalaran.DalaranConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.StringUtils;

public class ExpressionParser {

    private final String REQUEST_BODY_HEADER = "request.body.";

    private final String RESPONSE_BODY_HEADER = "response.body.";

    private final String REQUEST_HEAD_HEADER = "request.headers.";

    private final String RESPONSE_HEAD_HEADER = "response.headers";

    private final String JSON_PATH_HEADER = "$.";

    public String parseBodyPath(String expression) {
        if (StringUtils.contains(expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + REQUEST_BODY_HEADER)) {
            return StringUtils.substringAfter(expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + REQUEST_BODY_HEADER);
        }
        return StringUtils.substringAfter(expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + RESPONSE_BODY_HEADER);
    }

    public Object parse(String expression, Exchange exchange) {
        if (StringUtils.contains(expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + REQUEST_BODY_HEADER)) {
            return getValue(exchange.getIn(), expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + REQUEST_BODY_HEADER, ExpressionType.BODY);
        }
        if (StringUtils.contains(expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + RESPONSE_BODY_HEADER)) {
            return getValue(exchange.getOut(), expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + RESPONSE_BODY_HEADER, ExpressionType.BODY);
        }
        if (StringUtils.contains(expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + REQUEST_HEAD_HEADER)) {
            return getValue(exchange.getIn(), expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + REQUEST_HEAD_HEADER, ExpressionType.HEADER);
        }
        if (StringUtils.contains(expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + RESPONSE_HEAD_HEADER)) {
            return getValue(exchange.getOut(), expression, DalaranConstants.DALARAN_EXPRESSION_HEADER + RESPONSE_HEAD_HEADER, ExpressionType.HEADER);
        }
        return null;
    }

    private Object getValue(Message message, String expression, String header, ExpressionType type) {
        if (type == ExpressionType.BODY) {
            Object body = JSON.toJSON(message.getBody());
            String jsonPath = JSON_PATH_HEADER + StringUtils.substringAfter(expression, header);
            return JSONPath.eval(body, jsonPath);
        }
        if (type == ExpressionType.HEADER) {
            return message.getHeader(StringUtils.substringAfter(expression, header));
        }
        return null;
    }
}
