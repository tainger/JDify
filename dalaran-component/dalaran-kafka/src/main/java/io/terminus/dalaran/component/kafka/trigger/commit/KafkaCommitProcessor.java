package io.terminus.dalaran.component.kafka.trigger.commit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;

/**
 * Created by jingdi on 2019/5/20
 */
public class KafkaCommitProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        KafkaManualCommit commit = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
        if (commit != null) {
            commit.commitSync();
        }
    }
}
