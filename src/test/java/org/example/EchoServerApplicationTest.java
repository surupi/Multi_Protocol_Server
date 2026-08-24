package org.example;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EchoServerApplicationTest {

    @Test
    void testDefaultPortConfiguration() {
        EchoServerApplication app = new EchoServerApplication();
        assertEquals(1234, app.getPort());
        assertFalse(app.isHelp());
    }

    @Test
    void testCustomPortParsing() {
        EchoServerApplication app = new EchoServerApplication();
        JCommander commander = JCommander.newBuilder().addObject(app).build();
        commander.parse("-p", "8080");

        assertEquals(8080, app.getPort());
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
