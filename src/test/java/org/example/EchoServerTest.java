package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class EchoServerTest {
    private EchoServer server;
    private ExecutorService serverExecutor;

    @BeforeEach
    void setUp() throws InterruptedException {
        server = new EchoServer(0);
        serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.execute(() -> server.start());

        int attempts = 0;
        while (server.getLocalPort() == 0 && attempts++ < 20) {
            Thread.sleep(50);
        }
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
        if (serverExecutor != null) {
            serverExecutor.shutdownNow();
        }
    }

    @Test
    void testSingleClientEcho() throws IOException {
        int boundPort = server.getLocalPort();
        assertTrue(boundPort > 0, "Server port should be bound to a positive port number");

        try (
            Socket socket = new Socket("localhost", boundPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println("Hello Echo");
            String response = in.readLine();
            assertEquals("Hello Echo", response);

            out.println("Second Message");
            response = in.readLine();
            assertEquals("Second Message", response);
        }
    }

    @Test
    void testMultipleConcurrentClients() throws InterruptedException {
        int boundPort = server.getLocalPort();
        int clientCount = 5;
        ExecutorService clientsPool = Executors.newFixedThreadPool(clientCount);

        for (int i = 0; i < clientCount; i++) {
            final int clientId = i;
            clientsPool.execute(() -> {
                try (
                    Socket socket = new Socket("localhost", boundPort);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {
                    String msg = "Message from client " + clientId;
                    out.println(msg);
                    String resp = in.readLine();
                    assertEquals(msg, resp);
                } catch (IOException e) {
                    fail("Client " + clientId + " failed: " + e.getMessage());
                }
            });
        }

        clientsPool.shutdown();
        assertTrue(clientsPool.awaitTermination(5, TimeUnit.SECONDS), "All clients should finish within timeout");
    }
}
