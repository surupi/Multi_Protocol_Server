package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.handler.ClientHandler;
import org.example.handler.EchoHandler;

import java.io.IOException;
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
    private final ClientHandler handler;
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    public EchoServer(int port) {
        this(port, new EchoHandler());
    }

    public EchoServer(int port, ClientHandler handler) {
        this.port = port;
        this.handler = handler != null ? handler : new EchoHandler();
    }

    public void start() {
        this.threadPool = Executors.newCachedThreadPool();
        this.running = true;

        try {
            serverSocket = new ServerSocket(port);
            log.info("Starting Server on port {}", serverSocket.getLocalPort());

            while (running && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log.info("Client connected: {}", clientSocket.getRemoteSocketAddress());
                    threadPool.execute(() -> handler.handle(clientSocket));
                } catch (SocketException e) {
                    if (!running || serverSocket.isClosed()) {
                        log.info("Server stopped accepting connections.");
                        break;
                    }
                    log.error("Socket error accepting connection", e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to start Server on port {}", port, e);
        } finally {
            stop();
        }
    }

    public int getLocalPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : port;
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }
        this.running = false;
        log.info("Stopping Server...");

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
        log.info("Server stopped successfully.");
    }
}
