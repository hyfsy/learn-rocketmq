package com.hyf.rocketmq.message;

import java.util.UUID;

/**
 * @author baB_hyf
 * @date 2022/04/10
 */
public class CustomMessage {

    private String id;

    public CustomMessage() {
        this(UUID.randomUUID().toString());
    }

    public CustomMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
