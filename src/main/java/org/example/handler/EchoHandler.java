package org.example.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class EchoHandler implements ClientHandler {

    @Override
    public void handle(Socket clientSocket) {
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
}
