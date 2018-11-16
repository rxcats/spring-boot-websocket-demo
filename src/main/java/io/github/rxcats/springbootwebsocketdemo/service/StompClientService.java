package io.github.rxcats.springbootwebsocketdemo.service;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.springbootwebsocketdemo.message.ServerMessage;
import io.github.rxcats.springbootwebsocketdemo.message.WsResponseEntity;
import io.github.rxcats.springbootwebsocketdemo.ws.WsSessionHolder;

@Service
public class StompClientService {

    @Value("${server.port}")
    private Integer port;

    private final ObjectMapper objectMapper;

    @Autowired
    public StompClientService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String getUrl() {
        return "ws://127.0.0.1:" + port + "/chat";
    }

    public StompSession connect(String userId) {

        StandardWebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(simpleWebSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setInboundMessageSizeLimit(8192);

        StompSessionHandler sessionHandler = new MyStompSessionHandler(userId, objectMapper);

        try {
            return stompClient.connect(getUrl(), sessionHandler).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Slf4j
    public static class MyStompSessionHandler extends StompSessionHandlerAdapter {

        String userId;
        ObjectMapper objectMapper;

        public MyStompSessionHandler(String userId, ObjectMapper objectMapper) {
            this.userId = userId;
            this.objectMapper = objectMapper;
        }

        private void subscribeTopic(String topic, StompSession session) {
            session.subscribe(topic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return ServerMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    WebSocketSession webSocketSession = WsSessionHolder.getWebSocketSession(userId);

                    try {
                        String response = objectMapper.writeValueAsString(WsResponseEntity.of("", payload));
                        webSocketSession.sendMessage(new TextMessage(response));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    log.info("handleFrame:{}", payload);

                }

            });
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            subscribeTopic("/topic/messages", session);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            log.error("handleTransportError:{}", exception.getMessage());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            exception.printStackTrace();
        }
    }

}
