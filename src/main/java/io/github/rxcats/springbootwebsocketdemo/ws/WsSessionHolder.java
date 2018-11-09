package io.github.rxcats.springbootwebsocketdemo.ws;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.web.socket.WebSocketSession;

public class WsSessionHolder {

    private static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public static void put(String key, WebSocketSession session) {
        sessions.putIfAbsent(key, session);
    }

    public static void remove(String key) {
        sessions.remove(key);
    }

    public static WebSocketSession get(String key) {
        return sessions.get(key);
    }

    public static List<WebSocketSession> getList(Collection<String> keys) {
        return keys.stream()
            .map(key -> sessions.get(key))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
