package de.mbe.aws.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main (final String[] args) {

        var message = """
            hello, java 17
            """;

        LOGGER.info(message);
    }
}
