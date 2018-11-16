package io.github.rxcats.springbootwebsocketdemo.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import io.github.rxcats.springbootwebsocketdemo.message.ServerMessage;

@Controller
public class ChatController {

    @MessageMapping("/chat/{topic}")
    @SendTo("/topic/messages")
    public ServerMessage send(@DestinationVariable("topic") String topic, ServerMessage msg) {

        return msg;

    }

}
