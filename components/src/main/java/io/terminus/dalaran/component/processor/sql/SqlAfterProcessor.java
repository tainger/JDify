package io.terminus.dalaran.component.processor.sql;

import io.terminus.dalaran.ComponentConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlAfterProcessor implements Processor {

    private SQLOperationType operationType;

    private Boolean printSql;

    private final Logger logger = LoggerFactory.getLogger(SqlAfterProcessor.class);

    public SqlAfterProcessor(SQLOperationType operationType, Boolean printSql) {
        this.operationType = operationType;
        this.printSql = printSql;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (printSql) {
            logger.info(exchange.getIn().getHeader(ComponentConstants.SQL_QUERY).toString());
        }
        if (operationType == SQLOperationType.INSERT) {
            Object rows = exchange.getIn().getHeader(ComponentConstants.SQL_RETURN_KEYS);
            exchange.getOut().setBody(rows);
        }
        if (operationType == SQLOperationType.UPDATE) {
            Object rowCount = exchange.getIn().getHeader(ComponentConstants.SQL_UPDATE_COUNT);
            exchange.getOut().setBody(rowCount);
        }
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
