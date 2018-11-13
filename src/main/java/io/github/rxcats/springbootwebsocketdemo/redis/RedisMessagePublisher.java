package io.github.rxcats.springbootwebsocketdemo.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Service;

import io.github.rxcats.springbootwebsocketdemo.message.ResultCode;
import io.github.rxcats.springbootwebsocketdemo.message.SendMessageEntity;
import io.github.rxcats.springbootwebsocketdemo.message.WsResponseEntity;

@Service
public class RedisMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(Topic topic, String cmd, SendMessageEntity entity) {
        redisTemplate.convertAndSend(topic.getTopic(), WsResponseEntity.of(cmd, entity));
    }

}
