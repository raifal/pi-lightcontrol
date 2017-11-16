package de.rainerfaller.hsm.pi.lightcontrol;

import org.apache.log4j.Logger;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import javax.net.ssl.*;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class HelloClient {

    // https://github.com/jpmorganchase/cakeshop/blob/master/cakeshop-client-java/src/main/java/com/jpmorgan/cakeshop/client/ws/WebSocketClient.java

    @Value("${my.home.backend.port}")
    private String port;

    private static Logger logger = Logger.getLogger(HelloClient.class);

    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    public HelloClient() {
    }

    public ListenableFuture<StompSession> connect() throws NoSuchAlgorithmException, KeyManagementException {

// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        //  boolean trustAll = Boolean.getBoolean("org.eclipse.jetty.websocket.jsr356.ssl-trust-all");


        /*SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        Map<String, Object> props = new HashMap<>();
        props.put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);*/


        StandardWebSocketClient swsc = new StandardWebSocketClient();
        // swsc.setUserProperties(props);

        Transport webSocketTransport = new WebSocketTransport(swsc);

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> hostname.equals(hostname));

        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();

        String user = System.getProperty("hsm.user");
        String password = System.getProperty("hsm.secret");
        String hostname = System.getProperty("ipinterative_ip");
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        String url = "wss://" + hostname + ":" + port + "/hsm/lightcontrol-websocket";

        //stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setAutoStartup(true);
        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setDefaultHeartbeat(new long[]{60000, 60000});

        String plainCredentials = user + ":" + password;
        String base64Credentials = Base64.getEncoder().encodeToString(plainCredentials.getBytes());
        final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);


        return stompClient.connect(url, headers, new MyHandler(), hostname, port);
    }

    public void subscribeTopic(StompSession stompSession) throws ExecutionException, InterruptedException {

        stompSession.subscribe("/topic/lightcontrol", new StompFrameHandler() {
            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                logger.info("Received greeting " + new String((byte[]) o));
            }
        });

    }

    public void sendTestMessage(StompSession stompSession) {
        String jsonHello = "{ \"name\" : \"Nick\" }";
        stompSession.send("/app/testmessage", jsonHello.getBytes());
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public MyHandler() {
        }

        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            logger.info("Now connected");
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            logger.error("handle exception", exception);
            super.handleException(session, command, headers, payload, exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            if (exception instanceof ConnectionLostException) {
                // connection is lost
            }
            super.handleTransportError(session, exception);
        }
    }
}
