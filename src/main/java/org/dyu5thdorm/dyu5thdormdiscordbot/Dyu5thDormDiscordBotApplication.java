package org.dyu5thdorm.dyu5thdormdiscordbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Dyu5thDormDiscordBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(Dyu5thDormDiscordBotApplication.class, args);
    }

}
