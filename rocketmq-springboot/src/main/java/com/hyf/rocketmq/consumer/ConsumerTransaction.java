package com.hyf.rocketmq.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;

/**
 * @author baB_hyf
 * @date 2022/04/10
 */
@RocketMQTransactionListener(
        corePoolSize = 1,
        maximumPoolSize = 1,
        keepAliveTime = 60000L,
        blockingQueueSize = 2000,
        rocketMQTemplateBeanName = "rocketMQTemplate"
)
public class ConsumerTransaction implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(org.springframework.messaging.Message message, Object arg) {
        return null;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(org.springframework.messaging.Message message) {
        return RocketMQLocalTransactionState.COMMIT;
    }
}
