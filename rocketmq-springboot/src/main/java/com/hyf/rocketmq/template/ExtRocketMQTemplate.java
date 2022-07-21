package com.hyf.rocketmq.template;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * 扩展已有的 RocketMQTemplate 的配置
 *
 * @author baB_hyf
 * @date 2022/04/10
 */
@ExtRocketMQTemplateConfiguration(nameServer = ExtRocketMQTemplate.EXT_NAME_SERVER)
public class ExtRocketMQTemplate extends RocketMQTemplate {

    public static final String EXT_NAME_SERVER = "localhost:8765";
}
