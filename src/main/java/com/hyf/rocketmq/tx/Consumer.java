package com.hyf.rocketmq.tx;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

/**
 * @author baB_hyf
 * @date 2022/01/21
 */
public class Consumer {

    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-group");

        // ...
    }
}
