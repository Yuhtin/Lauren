package com.yuhtin.lauren;

import com.yuhtin.lauren.bot.DiscordBotLoader;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Startup {

    @Getter private static Lauren lauren;

    public static void main(String[] args) throws IOException {
        lauren = new Lauren();
        DiscordBotLoader.connect(lauren);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            lauren.onDisable();
        }));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String command;
        while ((command = reader.readLine()) != null) {
            if (command.equalsIgnoreCase("stop")) {
                lauren.onDisable();
                System.exit(0);
                break;
            } else {
                lauren.getLogger().warning("Unknown command: " + command);
            }
        }
    }

}
