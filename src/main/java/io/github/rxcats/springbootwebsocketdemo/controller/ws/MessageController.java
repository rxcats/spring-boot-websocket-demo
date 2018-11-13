package io.github.rxcats.springbootwebsocketdemo.controller.ws;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Scheduled;

import io.github.rxcats.springbootwebsocketdemo.message.SendMessageEntity;
import io.github.rxcats.springbootwebsocketdemo.message.SendMessageRequest;
import io.github.rxcats.springbootwebsocketdemo.redis.RedisMessagePublisher;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsController;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsMethod;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestBody;

@WsController(prefix = "/message")
public class MessageController {

    private final RedisMessagePublisher publisher;

    @Autowired
    public MessageController(RedisMessagePublisher publisher) {

        this.publisher = publisher;
    }

    @WsMethod(uri = "/send")
    public void sendMessage(@Valid @WsRequestBody SendMessageRequest request) {

        publisher.publish(new ChannelTopic("topic:1"), "/message/send", SendMessageEntity.of(request.getUserId(), request.getMessage()));

    }

    @Scheduled(fixedDelay = 5000L)
    public void scheduledSendMessage() {

        publisher.publish(new ChannelTopic("topic:1"), "/message/send", SendMessageEntity.of("-1", "just scheduled message"));

    }

}
