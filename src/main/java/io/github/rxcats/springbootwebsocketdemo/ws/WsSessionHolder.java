package io.github.rxcats.springbootwebsocketdemo.ws;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WsSessionHolder {

    private static Map<String, WebSocketSession> webSocketSessionMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, StompSession> stompSessionMap = Collections.synchronizedMap(new HashMap<>());

    public static void put(String key, WebSocketSession webSocketSession, StompSession stompSession) {
        webSocketSessionMap.putIfAbsent(key, webSocketSession);
        stompSessionMap.putIfAbsent(key, stompSession);
    }

    public static void remove(String key) {
        webSocketSessionMap.remove(key);
        stompSessionMap.remove(key);
    }

    public static WebSocketSession getWebSocketSession(String key) {
        return webSocketSessionMap.get(key);
    }

    public static StompSession getStompSession(String key) {
        return stompSessionMap.get(key);
    }

    public static boolean webSocketSessionContainsKey(String key) {
        return webSocketSessionMap.containsKey(key);
    }

    public static boolean stompSessionContainsKey(String key) {
        return stompSessionMap.containsKey(key);
    }

    public static Map<String, WebSocketSession> getAllWebSocketSession() {
        return webSocketSessionMap;
    }

    public static Map<String, StompSession> getAllstompSession() {
        return stompSessionMap;
    }

    public static List<WebSocketSession> getList(Collection<String> keys) {
        return keys.stream()
            .map(key -> webSocketSessionMap.get(key))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Scheduled(fixedDelay = 60_000L)
    public void clean() {
        List<String> keys = webSocketSessionMap.entrySet().stream()
            .filter(v -> !v.getValue().isOpen())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        for (String k : keys) {
            remove(k);
        }
    }

}
