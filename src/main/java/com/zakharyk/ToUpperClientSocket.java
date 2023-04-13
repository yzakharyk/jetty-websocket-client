package com.zakharyk;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.CountDownLatch;

@Slf4j
@WebSocket

public class ToUpperClientSocket {
    private Session session;
    private final CountDownLatch latch = new CountDownLatch(1);

    @OnWebSocketMessage
    public void onText(Session session, String message) {
        log.info("Message received from server:{}", message);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.info("Connected to server");
        this.session = session;
        latch.countDown();
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        log.error("Websocket error:{}", error.getMessage());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        log.info("Websocket closed. Code:{}. Reason:{}", statusCode, reason);
    }

    @SneakyThrows
    public void sendMessage(String str) {
        log.info("Message sent to server:{}", str);
        session.getRemote().sendString(str);

    }

    public CountDownLatch getLatch() {
        return latch;
    }

}
