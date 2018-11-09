package io.github.rxcats.springbootwebsocketdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication
public class SpringBootWebsocketDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebsocketDemoApplication.class, args);
    }
}
