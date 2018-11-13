package io.github.rxcats.springbootwebsocketdemo.controller.ws;

import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.springbootwebsocketdemo.message.WorldRequest;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsController;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsMethod;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestBody;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestHeader;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsSession;

@Slf4j
@WsController(prefix = "/hello")
public class HelloController {

    @WsMethod(uri = "/world", description = "hello")
    public String world(@WsRequestBody WorldRequest request, @WsSession WebSocketSession session, @WsRequestHeader(value = "type") int type) {
        log.info("req : {}", request);
        log.info("session : {}", session);
        log.info("type : {}", type);
        return "world";
    }

    @WsMethod(uri = "/void", description = "void")
    public void voidReturn() {
        log.info("void");
    }

}
