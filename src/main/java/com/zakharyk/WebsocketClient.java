package com.zakharyk;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.Scanner;

public class WebsocketClient {
    private static final String WEBSOCKET_SERVER_ENDPOINT = "wss://localhost:10444";
    public static final long HEART_BEAT_FREQUENCY_MS = 5000L;

    public static void main(String[] args) throws Exception {
        // in case of untrusted ssl certificate usage
        SslContextFactory ssl = new SslContextFactory.Client.Client(true);
        WebSocketClient client = new WebSocketClient(new HttpClient(ssl));
        // set the max message size
        client.getPolicy().setMaxTextMessageSize(100000000);

        ToUpperClientSocket socket = new ToUpperClientSocket();
        client.start();

        ClientUpgradeRequest request = new ClientUpgradeRequest();
        client.connect(socket, new URI(WEBSOCKET_SERVER_ENDPOINT), request);
        // latch is used here to await connection with the server
        socket.getLatch().await();
        // format of heartbeats could be adjusted
        sendHeartbeats(socket);
        sendMessageFromConsole(socket);
    }

    private static void sendMessageFromConsole(ToUpperClientSocket socket) {
        while (true) {
            var message = new Scanner(System.in).nextLine();
            socket.sendMessage(message);
        }
    }


    private static void sendHeartbeats(ToUpperClientSocket socket) {
        try {
            var heartBeatText = "HEARTBEAT";
            new Thread(() -> {
                while (true) {
                    socket.sendMessage(heartBeatText);
                    try {
                        Thread.sleep(HEART_BEAT_FREQUENCY_MS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}