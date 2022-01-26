package com.hyf.rocketmq.tx;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author baB_hyf
 * @date 2022/01/21
 */
public class Producer {

    public static void main(String[] args) throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer("producer-group");

        // 设置事务回查使用的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        producer.setExecutorService(executorService);
        // producer.setSendMsgTimeout(5000);

        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                // LocalTransactionState.UNKNOW; // half 事务
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                String transactionId = msg.getTransactionId();
                return null;
            }
        });

        Message message = new Message("topic-test", "tag-test", "Hello World Tx".getBytes(StandardCharsets.UTF_8));

        producer.sendMessageInTransaction(message, 1 /* arg */);

    }
}
