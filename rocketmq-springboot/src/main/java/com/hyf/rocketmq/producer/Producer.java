package com.hyf.rocketmq.producer;

import com.hyf.rocketmq.message.CustomMessage;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author baB_hyf
 * @date 2022/04/10
 */
@Component
public class Producer {

    public static final String TOPIC = "topic";

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void syncSend() {
        CustomMessage customMessage = new CustomMessage();

        // 同步发送
        rocketMQTemplate.syncSend(TOPIC, customMessage);

        // 异步发送
        rocketMQTemplate.asyncSend(TOPIC, customMessage, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        // 直接发送，不管结果
        rocketMQTemplate.sendOneWay(TOPIC, customMessage);

        Message<CustomMessage> message = MessageBuilder.withPayload(customMessage)
                .build();

        // 批量发送
        rocketMQTemplate.syncSend(TOPIC, Collections.singletonList(message), 1000L);

        // 延迟发送
        rocketMQTemplate.syncSend(TOPIC, message, 1000L, 2 /* delayLevel */);

        // 有序发送
        // 默认byHash SelectMessageQueueByHash，see RocketMQTemplate#messageQueueSelector
        rocketMQTemplate.sendOneWayOrderly(TOPIC, customMessage, customMessage.getId());

        // 事务消息
        rocketMQTemplate.sendMessageInTransaction(TOPIC, message, message.getPayload().getId());
    }
}
