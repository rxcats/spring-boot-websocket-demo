package io.github.rxcats.springbootwebsocketdemo.controller.ws;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.springbootwebsocketdemo.message.LoginRequest;
import io.github.rxcats.springbootwebsocketdemo.service.StompClientService;
import io.github.rxcats.springbootwebsocketdemo.ws.WsSessionHolder;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsController;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsMethod;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestBody;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsSession;

@WsController(prefix = "/session")
public class SessionController {

    private final StompClientService stompClientService;

    @Autowired
    public SessionController(StompClientService stompClientService) {
        this.stompClientService = stompClientService;
    }

    @WsMethod(uri = "/login")
    public void login(@WsSession WebSocketSession session, @Valid @WsRequestBody LoginRequest request) {

        if (!WsSessionHolder.webSocketSessionContainsKey(request.getUserId())) {
            StompSession connect = stompClientService.connect(request.getUserId());
            WsSessionHolder.put(request.getUserId(), session, connect);
        }

    }

    @WsMethod(uri = "/logout")
    public void logout(@WsSession WebSocketSession session, @Valid @WsRequestBody LoginRequest request) {

        WsSessionHolder.remove(request.getUserId());

    }

}
