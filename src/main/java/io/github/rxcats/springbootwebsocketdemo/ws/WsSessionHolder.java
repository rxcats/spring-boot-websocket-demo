package io.github.rxcats.springbootwebsocketdemo.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WsSessionHolder {

    private static Map<String, WebSocketSession> sessions = Collections.synchronizedMap(new HashMap<>());

    public static void put(String key, WebSocketSession session) {
        sessions.putIfAbsent(key, session);
    }

    public static void remove(String key) {
        sessions.remove(key);
    }

    public static WebSocketSession get(String key) {
        return sessions.get(key);
    }

    public static boolean containsKey(String key) {
        return sessions.containsKey(key);
    }

    public static Map<String, WebSocketSession> getAll() {
        return sessions;
    }

    public static List<WebSocketSession> getList(Collection<String> keys) {
        return keys.stream()
            .map(key -> sessions.get(key))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Scheduled(fixedDelay = 60_000L)
    public void clean() {
        List<String> keys = sessions.entrySet().stream()
            .filter(v -> !v.getValue().isOpen())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        for (String k : keys) {
            remove(k);
        }
    }

}
