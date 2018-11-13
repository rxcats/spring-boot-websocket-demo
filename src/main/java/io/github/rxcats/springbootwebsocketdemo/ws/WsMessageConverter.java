package io.github.rxcats.springbootwebsocketdemo.ws;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

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
    private final Validator validator;

    @Autowired
    public WsMessageConverter(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
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
        if (!session.isOpen()) {
            return;
        }

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

        String cmd = objectMapper.convertValue(request.get(FIELD_CMD), String.class);

        WsControllerEntity wsController = WsControllerHolder.getWsController(cmd);

        log.info("wsController : {}", wsController);

        try {

            Object[] args = convertMethodParam(wsController, request, session);

            Object result = wsController.getMethod().invoke(wsController.getBean(), args);

            var response = WsResponseEntity.of(cmd, result);

            log.info("response : {}", response);

            sendMessage(session, response);

        } catch (Exception e) {

            log.error("error : {}", e.getMessage());

            var response = WsResponseEntity.error(cmd, e.getMessage());

            log.info("response : {}", response);

            sendMessage(session, response);
        }

    }

    private Object[] convertMethodParam(WsControllerEntity wsController, Map<String, Object> request, WebSocketSession session) {

        Class<?>[] parameterTypes = wsController.getMethod().getParameterTypes();
        Annotation[][] parameterAnnotations = wsController.getMethod().getParameterAnnotations();

        Object[] paramValues = new Object[parameterTypes.length];

        if (parameterTypes.length > 0) {

            for (int i = 0; i < parameterTypes.length; i++) {

                Class<?> paramType = parameterTypes[i];

                for (int j = 0; j < parameterAnnotations[i].length; j++) {

                    if (parameterAnnotations[i][j] instanceof WsRequestBody) {
                        paramValues[i] = objectMapper.convertValue(request.get(FIELD_BODY), paramType);

                        boolean useValidator = false;

                        for (int k = 0; k < parameterAnnotations[i].length; k++) {
                            if (!useValidator && parameterAnnotations[i][k] instanceof Valid) {
                                useValidator = true;
                            }
                        }

                        if (useValidator) {
                            Set<ConstraintViolation<Object>> validate = validator.validate(paramValues[i]);

                            if (!validate.isEmpty()) {
                                for (ConstraintViolation constraintViolation : validate) {
                                    log.warn("validate : {} {} {}", constraintViolation.getRootBeanClass().getSimpleName(),
                                        constraintViolation.getPropertyPath(),
                                        constraintViolation.getMessage());
                                }

                                throw new IllegalArgumentException("validation error");
                            }
                        }

                    } else if (parameterAnnotations[i][j] instanceof WsRequestHeader) {

                        // @formatter:off
                        Map<String, Object> headers = objectMapper.convertValue(request.get(FIELD_HEADERS), new TypeReference<Map<String, Object>>() {});
                        // @formatter:on

                        String key = ((WsRequestHeader) parameterAnnotations[i][j]).value();
                        paramValues[i] = objectMapper.convertValue(headers.get(key), paramType);

                    } else if (parameterAnnotations[i][j] instanceof WsSession) {
                        paramValues[i] = session;
                    }

                }
            }
        }

        return paramValues;
    }

}
