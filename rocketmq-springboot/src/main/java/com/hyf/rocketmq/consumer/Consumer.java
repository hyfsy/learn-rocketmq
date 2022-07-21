package com.hyf.rocketmq.consumer;

import com.hyf.rocketmq.message.CustomMessage;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author baB_hyf
 * @date 2022/04/10
 */
@Component
@RocketMQMessageListener(
        consumerGroup = "test-consumer-group",
        topic = "topic",
        consumeMode = ConsumeMode.CONCURRENTLY, // 并发消费
        // consumeMode = ConsumeMode.ORDERLY, // 有序消费
        messageModel = MessageModel.BROADCASTING, // 广播消费
        // messageModel = MessageModel.CLUSTERING, // 单消费者消费

        consumeThreadMax = 8
)
public class Consumer implements RocketMQListener<CustomMessage> {

    @Override
    public void onMessage(CustomMessage customMessage) {
        System.out.println("receive message: " + customMessage);
    }
}
