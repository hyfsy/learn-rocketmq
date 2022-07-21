package com.hyf.rocketmq.hello;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.utils.MessageUtil;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/01/19
 */
public class Consumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-group");
        consumer.setNamesrvAddr("localhost:9876");

        // 从某个时间点开始消费
        consumer.setConsumeTimestamp(UtilAll.timeMillisToHumanString3(System.currentTimeMillis() - (1000 * 60 * 30)));

        // 设置Topic **第一次** 进行消费的消费进度

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        // 从队列尾部消费，跳过历史消息
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 从队列头部消费，包括历史消息
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(20);

        // 订阅频道
        consumer.subscribe("TopicTest", "*" /* tag match */);
        consumer.subscribe("TopicTest", "TAG1 || TAG2");
        consumer.subscribe("TopicTest2", MessageSelector
                .bySql("p1 is not null and p1 > 1 or p1 between 0 and 10 or (not p1) and p1 <> 1 and p1 in ('1', '2')"));

        // 并发消费
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            private final DefaultMQProducer replyProducer;

            {
                replyProducer = new DefaultMQProducer("producerGroup");
                replyProducer.start();
            }

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                for (MessageExt messageExt : msgs) {

                    String msg = new String(messageExt.getBody());
                    System.out.println(msg);

                    try {
                        Message replyMessage = MessageUtil.createReplyMessage(messageExt, "reply message".getBytes(StandardCharsets.UTF_8));
                        replyProducer.send(replyMessage);
                    } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 顺序消费
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {

                if (true) {
                    consumer.setSuspendCurrentQueueTimeMillis(3000);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }

                // 默认会重复消费16次，若都失败，消息会投递到DLQ死信队列，应用可以监控死信队列来做人工干预
                // 死信队列若没有默认会创建一个Topic命名为"%DLQ%+consumerGroup"的
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        consumer.start();

        // consumer.shutdown();

        // 默认集群模式，保证组名相同
        // 集群，只需要水平扩展
        DefaultMQPushConsumer consumer2 = new DefaultMQPushConsumer("consumer-group");
        consumer2.setNamesrvAddr("localhost:9876");
        consumer2.setInstanceName("consumerInstance");

        // 广播模式
        consumer2.setMessageModel(MessageModel.CLUSTERING);
        consumer2.setMessageModel(MessageModel.BROADCASTING);
    }
}
