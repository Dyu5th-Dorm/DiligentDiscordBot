package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevPropertyUpdater {

    @Value("${dev.config.discord-file:src/main/resources/discord-dev.properties}")
    private String discordPropertyFile;

    public synchronized void updateIfBlank(String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        Path path = Path.of(discordPropertyFile);
        if (!Files.exists(path)) {
            log.warn("[DevPropertyUpdater] Property file '{}' not found. Skipping update for key {}.", discordPropertyFile, key);
            return;
        }

        Properties props = new Properties();

        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (IOException exception) {
            log.warn("[DevPropertyUpdater] Failed to read properties from {}: {}", discordPropertyFile, exception.getMessage());
            return;
        }

        String current = props.getProperty(key);
        if (current != null && !current.isBlank()) {
            return;
        }

        props.setProperty(key, value);

        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            props.store(writer, "Auto-updated by DevPropertyUpdater");
        } catch (IOException exception) {
            log.warn("[DevPropertyUpdater] Failed to write properties to {}: {}", discordPropertyFile, exception.getMessage());
        }
    }
}
