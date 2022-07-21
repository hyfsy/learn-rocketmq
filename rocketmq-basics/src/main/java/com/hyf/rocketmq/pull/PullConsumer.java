package com.hyf.rocketmq.pull;

import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Arrays;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/01/26
 */
public class PullConsumer {

    public static void main(String[] args) throws MQClientException {
        MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("consumer-group");
        scheduleService.setMessageModel(MessageModel.BROADCASTING);

        scheduleService.registerPullTaskCallback("topic", new PullTaskCallback() {
            @Override
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer pullConsumer = context.getPullConsumer();
                try {
                    long offset = pullConsumer.fetchConsumeOffset(mq, false);
                    if (offset < 0) offset = 0;

                    PullResult result = pullConsumer.pull(mq, "*", offset, 32, 3000L);
                    PullStatus pullStatus = result.getPullStatus();
                    if (PullStatus.FOUND == pullStatus) {
                        // ...
                        List<MessageExt> msgFoundList = result.getMsgFoundList();
                        for (MessageExt messageExt : msgFoundList) {
                            System.out.println(Arrays.toString(messageExt.getBody()));
                        }
                    }
                    pullConsumer.updateConsumeOffset(mq, result.getNextBeginOffset());
                    context.setPullNextDelayTimeMillis(100);

                } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        scheduleService.start();

    }
}
