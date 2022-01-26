package com.hyf.rocketmq.hello;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.exception.RequestTimeoutException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/01/19
 */
public class Producer {

    public static void main(String[] args)
            throws MQClientException, RemotingException, InterruptedException, MQBrokerException, RequestTimeoutException {

        DefaultMQProducer producer = new DefaultMQProducer("producer-group");
        producer.setNamesrvAddr("localhost:9876");

        producer.setRetryTimesWhenSendAsyncFailed(3);

        // 消息体达到多少阈值开始进行压缩
        producer.setCompressMsgBodyOverHowmuch(Integer.MAX_VALUE);

        // 确保一个Producer/Consumer Group对应的JVM只启动一个Producer/Consumer实例
        producer.start();

        for (int i = 0; i < 10; i++) {
            // 最大256KB
            Message message = new Message("topic-test", "tag", ("Hello World" + i).getBytes(StandardCharsets.UTF_8));

            // 同步发送消息
            producer.send(message);

            // 只发送，不处理响应
            producer.sendOneway(message);

            // 异步发送消息
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("onSuccess: " + sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    System.out.println("onException: " + e);
                }
            });

            // 发送到相同的队列内，保证多个消息有序
            producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    return mqs.get((int) arg % 2);
                }
            }, i /* 参数给select方法使用 */);

            // 批量发送，用于发送大批量的小消息
            // 不支持延时消息，消息总大小不应该大于1MB
            producer.send(Collections.singletonList(message));

            // 延时消息，只支持指定的延时级别，不支持指定时间
            message.setDelayTimeLevel(3);
            // 设置属性，消费者可通过属性对消息进行过滤
            message.putUserProperty("p1", "p1-name");

            long ttl = 3000L;
            // 同步发送消息
            producer.request(message, ttl);
            // 异步发送消息
            producer.request(message, new RequestCallback() {
                @Override
                public void onSuccess(Message message) {
                    System.out.println(message.getTopic());
                }

                @Override
                public void onException(Throwable e) {
                    e.printStackTrace();
                }
            }, ttl);
        }

        producer.shutdown();

        // 默认集群模式，保证组名相同
        // 集群，只需要水平扩展
        DefaultMQProducer producer2 = new DefaultMQProducer("producer-group");
        producer2.setNamesrvAddr("localhost:9876");
        producer2.setInstanceName("producerInstance");

        // OpenMessaging
    }
}
