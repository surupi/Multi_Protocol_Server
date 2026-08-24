package org.example.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GreetingHandlerTest {

    private Socket mockSocket;

    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);
    }

    @Test
    void testGreetingHandlerSuccess() throws IOException {
        String input = "NAME John Doe\nLOCATION Earth\nGREET\nQUIT\n";
        String expectedOutput = "200 server ready\n201 NAME ok\n201 LOCATION ok\nHello John Doe of Earth\n202 Bye\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        GreetingHandler handler = new GreetingHandler(45);
        handler.handle(mockSocket);
        String actualOutput = outputStream.toString();

        assertEquals(expectedOutput, actualOutput, "Greeting execution successful");
    }

    @Test
    void testGreetingHandlerMissingName() throws IOException {
        String input = "LOCATION Earth\nGREET\nQUIT\n";
        String expectedOutput = "200 server ready\n201 LOCATION ok\n400 Bad Request\n202 Bye\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        GreetingHandler handler = new GreetingHandler(45);
        handler.handle(mockSocket);
        String actualOutput = outputStream.toString();

        assertEquals(expectedOutput, actualOutput, "missing NAME command");
    }

    @Test
    void testGreetingHandlerMissingLocation() throws IOException {
        String input = "NAME John Doe\nGREET\nQUIT\n";
        String expectedOutput = "200 server ready\n201 NAME ok\n400 Bad Request\n202 Bye\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        GreetingHandler handler = new GreetingHandler(45);
        handler.handle(mockSocket);
        String actualOutput = outputStream.toString();

        assertEquals(expectedOutput, actualOutput, "missing LOCATION command");
    }

    @Test
    void testGreetingHandlerMissingNameAndLocation() throws IOException {
        String input = "GREET\nQUIT\n";
        String expectedOutput = "200 server ready\n400 Bad Request\n202 Bye\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        GreetingHandler handler = new GreetingHandler(45);
        handler.handle(mockSocket);
        String actualOutput = outputStream.toString();

        assertEquals(expectedOutput, actualOutput, "missing NAME and LOCATION commands");
    }

    @Test
    void testGreetingHandlerIncorrectCommand() throws IOException {
        String input = "Ice Cream\nQUIT\n";
        String expectedOutput = "200 server ready\n400 Bad Request\n202 Bye\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        GreetingHandler handler = new GreetingHandler(45);
        handler.handle(mockSocket);
        String actualOutput = outputStream.toString();

        assertEquals(expectedOutput, actualOutput, "invalid command handling");
    }
}
