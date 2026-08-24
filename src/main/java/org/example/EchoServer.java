package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Slf4j
public class EchoServer {
    private final int port;
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() {
        this.threadPool = Executors.newCachedThreadPool();
        this.running = true;

        try {
            serverSocket = new ServerSocket(port);
            log.info("Starting EchoServer on port {}", serverSocket.getLocalPort());

            while (running && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log.info("Client connected: {}", clientSocket.getRemoteSocketAddress());
                    threadPool.execute(() -> handleClient(clientSocket));
                } catch (SocketException e) {
                    if (!running || serverSocket.isClosed()) {
                        log.info("EchoServer stopped accepting connections.");
                        break;
                    }
                    log.error("Socket error accepting connection", e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to start EchoServer on port {}", port, e);
        } finally {
            stop();
        }
    }

    public int getLocalPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : port;
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                log.debug("Received from client {}: {}", clientSocket.getRemoteSocketAddress(), inputLine);
                writer.println(inputLine);
            }
        } catch (IOException e) {
            log.debug("Client connection ended: {}", e.getMessage());
        } finally {
            try {
                clientSocket.close();
                log.info("Client connection closed: {}", clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                log.error("Error closing client socket", e);
            }
        }
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }
        this.running = false;
        log.info("Stopping EchoServer...");

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("Error closing server socket", e);
            }
        }

        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        log.info("EchoServer stopped successfully.");
    }
}
