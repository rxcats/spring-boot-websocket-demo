package io.github.rxcats.springbootwebsocketdemo.ws;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.springbootwebsocketdemo.message.WsResponseEntity;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestBody;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsRequestHeader;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsSession;

@Slf4j
@Component
public class WsMessageConverter {
    private final static String FIELD_CMD = "cmd";
    private final static String FIELD_HEADERS = "headers";
    private final static String FIELD_BODY = "body";

    private final ObjectMapper objectMapper;

    @Autowired
    public WsMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String toJson(WsResponseEntity<Object> response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.warn("write value as json error : {}", e.getMessage());
            return "";
        }
    }

    private void sendMessage(WebSocketSession session, WsResponseEntity<Object> response) {
        try {
            session.sendMessage(new TextMessage(toJson(response)));
        } catch (IOException e) {
            log.error("send message error : {}", e.getMessage());
        }
    }

    public void execute(WebSocketSession session, String payload) {

        if (StringUtils.isEmpty(payload)) {
            throw new IllegalArgumentException("payload is empty");
        }

        Map<String, Object> request;

        try {
            // @formatter:off
            request = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            log.info("request : {}", request);
            // @formatter:on
        } catch (IOException e) {
            sendMessage(session, WsResponseEntity.error("", "invalid request"));
            log.error("error : {}", e.getMessage());
            return;
        }

        String cmd = (String) request.get(FIELD_CMD);

        WsControllerEntity wsController = WsControllerHolder.getWsController(cmd);

        log.info("wsController : {}", wsController);

        Object[] args = convertMethodParam(wsController, request, session);

        Object result = null;
        try {
            result = wsController.getMethod().invoke(wsController.getBean(), args);
        } catch (Exception e) {
            log.error("error : {}", e.getMessage());
        }

        var response = WsResponseEntity.of(cmd, result);
        log.info("response : {}", response);

        sendMessage(session, response);

    }

    private Object[] convertMethodParam(WsControllerEntity wsController, Map<String, Object> request, WebSocketSession session) {

        Class<?>[] parameterTypes = wsController.getMethod().getParameterTypes();
        Annotation[][] parameterAnnotations = wsController.getMethod().getParameterAnnotations();

        Object[] paramValues = new Object[parameterTypes.length];

        Annotation[] annotations = Arrays.stream(parameterAnnotations)
            .map(v -> v[0])
            .toArray(Annotation[]::new);

        if (parameterTypes.length > 0) {

            for (int i = 0; i < parameterTypes.length; i++) {

                Class<?> paramType = parameterTypes[i];

                if (annotations[i] instanceof WsRequestBody) {
                    paramValues[i] = objectMapper.convertValue(request.get(FIELD_BODY), paramType);
                } else if (annotations[i] instanceof WsRequestHeader) {

                    // @formatter:off
                    Map<String, Object> headers = objectMapper.convertValue(request.get(FIELD_HEADERS), new TypeReference<Map<String, Object>>() {});
                    // @formatter:on

                    String key = ((WsRequestHeader) annotations[i]).value();
                    paramValues[i] = objectMapper.convertValue(headers.get(key), paramType);

                } else if (annotations[i] instanceof WsSession) {
                    paramValues[i] = session;
                }
            }
        }

        return paramValues;

    }

}
