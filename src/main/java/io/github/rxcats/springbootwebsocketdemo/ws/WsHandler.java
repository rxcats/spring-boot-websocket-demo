package io.github.rxcats.springbootwebsocketdemo.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WsHandler extends TextWebSocketHandler {

    private final WsMessageConverter wsMessageConverter;

    @Autowired
    public WsHandler(WsMessageConverter wsMessageConverter) {
        this.wsMessageConverter = wsMessageConverter;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection established");
        log.info("session : {}", session);
        //super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("receive message");
        log.info("session : {}", session);
        log.info("message : {}", message);
        wsMessageConverter.execute(session, message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("transport error");
        log.info("session : {}", session);
        //super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection closed");
        log.info("session : {}", session);
        //super.afterConnectionClosed(session, status);
    }
}
