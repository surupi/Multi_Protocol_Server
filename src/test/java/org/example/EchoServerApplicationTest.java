package org.example;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EchoServerApplicationTest {

    @Test
    void testDefaultPortConfiguration() {
        EchoServerApplication app = new EchoServerApplication();
        assertEquals(1234, app.getPort());
        assertEquals("echo", app.getMode());
        assertEquals(45, app.getTimeout());
        assertFalse(app.isHelp());
    }

    @Test
    void testCustomPortAndModeParsing() {
        EchoServerApplication app = new EchoServerApplication();
        JCommander commander = JCommander.newBuilder().addObject(app).build();
        commander.parse("-p", "8080", "--mode", "greeting", "-t", "60");

        assertEquals(8080, app.getPort());
        assertEquals("greeting", app.getMode());
        assertEquals(60, app.getTimeout());
        assertFalse(app.isHelp());
    }

    @Test
    void testHelpFlagParsing() {
        EchoServerApplication app = new EchoServerApplication();
        JCommander commander = JCommander.newBuilder().addObject(app).build();
        commander.parse("--help");

        assertTrue(app.isHelp());
    }
}
