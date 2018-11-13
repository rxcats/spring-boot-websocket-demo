package io.github.rxcats.springbootwebsocketdemo.controller.ws;

import javax.validation.Valid;

import org.springframework.web.socket.WebSocketSession;

import io.github.rxcats.springbootwebsocketdemo.message.LoginRequest;
import io.github.rxcats.springbootwebsocketdemo.ws.WsSessionHolder;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsController;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsMethod;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestBody;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsSession;

@WsController(prefix = "/session")
public class SessionController {

    @WsMethod(uri = "/login")
    public void login(@WsSession WebSocketSession session, @Valid @WsRequestBody LoginRequest request) {

        WsSessionHolder.put(request.getUserId(), session);

    }

    @WsMethod(uri = "/logout")
    public void logout(@WsSession WebSocketSession session, @Valid @WsRequestBody LoginRequest request) {

        WsSessionHolder.remove(request.getUserId());

    }

}
