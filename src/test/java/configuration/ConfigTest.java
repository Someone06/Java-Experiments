package configuration;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static configuration.ConfigBase.ConfigBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTest {

    private final static Path resources = Path.of("src", "test", "resources");

    @Test
    void itWorks() throws IOException {
        final var properties = new Properties();
        properties.load(new FileInputStream(
                resources.resolve("config.properties").toFile()));

        final var intKey = new ConfigurationKey("/test/int");
        final var stringKey = new ConfigurationKey("test", "string");
        final var builder = ConfigBuilder.create();
        builder.add(intKey, Integer.class, 1);
        builder.add(stringKey, String.class, "hello");
        final var config = new ConfigFromProperties(
                builder.build(), properties);

        assertEquals(1, config.getValue(intKey, Integer.class));
        assertEquals("file", config.getValue(stringKey, String.class));
    }
}
