package io.terminus.dalaran.component.processor.sql;

import io.terminus.dalaran.ComponentConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SqlBeforeProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(SqlBeforeProcessor.class);

    private SqlConfig sqlConfig;

    private static final String SQL_PREFIX = ":#";

    public SqlBeforeProcessor(SqlConfig sqlConfig) {
        this.sqlConfig = sqlConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(ComponentConstants.SQL_RETRIEVE_GENERATED_KEYS, true);
        if (sqlConfig.getPreHandle()) {
            String sql = sqlConfig.getSql();
            logger.info("before handle: " + sql);
            Map<String, Object> body = exchange.getIn().getBody(Map.class);
            for (Map.Entry<String, Object> entry: body.entrySet()) {
                if (StringUtils.contains(sql, SQL_PREFIX + entry.getKey())) {
                    sql = StringUtils.replace(sql, SQL_PREFIX + entry.getKey(), entry.getValue().toString());
                }
            }
            logger.info("after handle: " + sql);
            exchange.getOut().setHeader(ComponentConstants.PRE_HANDLE_SQL, sql);
        }
    }
}
