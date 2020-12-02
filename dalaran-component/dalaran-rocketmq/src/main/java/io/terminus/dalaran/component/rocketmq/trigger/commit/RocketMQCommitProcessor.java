package io.terminus.dalaran.component.rocketmq.trigger.commit;

import io.terminus.dalaran.camel.component.rocketmq.RocketMQManualCommit;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by jingdi on 2019/5/20
 */
public class RocketMQCommitProcessor implements Processor {

    private final String ROCKET_MQ_MANUAL_COMMIT = "ROCKET_MQ_MANUAL_COMMIT";

    @Override
    public void process(Exchange exchange) throws Exception {
        RocketMQManualCommit commit = exchange.getIn().getHeader(ROCKET_MQ_MANUAL_COMMIT, RocketMQManualCommit.class);
        if (commit != null) {
            commit.getConsumer().updateConsumeOffset(commit.getMessageQueue(), commit.getOffset());
        }
    }
}
