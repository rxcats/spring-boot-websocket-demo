package io.github.rxcats.springbootwebsocketdemo.redis;

import org.springframework.data.redis.listener.Topic;

import io.github.rxcats.springbootwebsocketdemo.message.SendMessageEntity;

public interface MessagePublisher {
    void publish(Topic topic, String cmd, SendMessageEntity entity);
}
