package io.github.rxcats.springbootwebsocketdemo.controller.ws;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.springbootwebsocketdemo.message.SendMessageRequest;
import io.github.rxcats.springbootwebsocketdemo.message.ServerMessage;
import io.github.rxcats.springbootwebsocketdemo.service.StompClientService;
import io.github.rxcats.springbootwebsocketdemo.ws.WsSessionHolder;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsController;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsMethod;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestBody;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsSession;

@WsController(prefix = "/message")
public class MessageController {

    private final StompClientService stompClientService;

    @Autowired
    public MessageController(StompClientService stompClientService) {
        this.stompClientService = stompClientService;
    }

    @WsMethod(uri = "/send")
    public void sendMessage(@WsSession WebSocketSession session, @Valid @WsRequestBody SendMessageRequest request) {
        ServerMessage msg = new ServerMessage();
        msg.setFrom(request.getUserId());
        msg.setMessage(request.getMessage());
        msg.setTopic("/app/chat/hello");

        if (!WsSessionHolder.webSocketSessionContainsKey(request.getUserId())) {
            StompSession connect = stompClientService.connect(request.getUserId());
            WsSessionHolder.put(request.getUserId(), session, connect);
        }

        StompSession stompSession = WsSessionHolder.getStompSession(request.getUserId());
        stompSession.send("/app/chat/hello", msg);
    }

}
