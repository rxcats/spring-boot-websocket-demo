package io.github.rxcats.springbootwebsocketdemo.message;

import lombok.Data;

@Data
public class SendMessageEntity {

    String userId;

    String message;

    long timestamp;

    public static SendMessageEntity of(String userId, String message) {
        var entity = new SendMessageEntity();
        entity.userId = userId;
        entity.message = message;
        entity.timestamp = System.currentTimeMillis();
        return entity;
    }

}
