package com.hyf.rocketmq.consumer;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author baB_hyf
 * @date 2022/04/10
 */
@Component
@RocketMQMessageListener(
        consumerGroup = "test-consumer-group",
        topic = "topic"
)
public class ConsumerExt implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt messageExt) {
        Message message = RocketMQUtil.convertToSpringMessage(messageExt);
        System.out.println(message);
    }
}
