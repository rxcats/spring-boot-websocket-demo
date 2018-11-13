package io.github.rxcats.springbootwebsocketdemo.redis;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.springbootwebsocketdemo.ws.WsSessionHolder;

@Slf4j
@Service
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    @Autowired
    public RedisMessageSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        WsSessionHolder.getAll()
            .entrySet()
            .parallelStream()
            .forEach(entry -> {

                if (!entry.getValue().isOpen()) {
                    return;
                }

                try {
                    var body = new String(message.getBody());

                    // @formatter:off
                    Map<String, Object> object = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
                    Map<String, Object> result = objectMapper.convertValue(object.get("result"), new TypeReference<Map<String, Object>>() {});
                    String userId = objectMapper.convertValue(result.get("userId"), String.class);
                    // @formatter:on

                    if (entry.getKey().equals(userId)) {
                        return;
                    }

                    entry.getValue().sendMessage(new TextMessage(body));
                    log.info("subscribe message : {}", body);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

}
