package io.github.rxcats.springbootwebsocketdemo.ws;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsController;
import io.github.rxcats.springbootwebsocketdemo.ws.annotation.WsMethod;

@Slf4j
@Component
public class WsControllerHolder {

    private static final Map<String, WsControllerEntity> wsControllerHolder = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;

    @Autowired
    public WsControllerHolder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.initialize();
    }

    private void initialize() {
        Map<String, Object> list = applicationContext.getBeansWithAnnotation(WsController.class);

        list.forEach((k, v) -> {

            WsController clazz = v.getClass().getDeclaredAnnotation(WsController.class);
            String prefix = clazz.prefix();

            Method[] wsMethods = v.getClass().getMethods();

            for (var method : wsMethods) {
                WsMethod wsMethod = method.getDeclaredAnnotation(WsMethod.class);
                if (wsMethod != null && !wsMethod.uri().isBlank()) {
                    wsControllerHolder.putIfAbsent(prefix + wsMethod.uri(), WsControllerEntity.of(v, method));
                }
            }

        });

        wsControllerHolder.forEach((k, v) -> log.info("wsMapped [{}] [{}]", k, v));

    }

    public static WsControllerEntity getWsController(String uri) {
        return wsControllerHolder.get(uri);
    }

}
