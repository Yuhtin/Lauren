package com.yuhtin.lauren.startup;

import com.yuhtin.lauren.Lauren;
import lombok.Getter;

import java.util.Scanner;

public class Startup {

    @Getter private static final Lauren lauren = new Lauren("Lauren");

    public static void main(String[] args) {

        try {
            lauren.onLoad();
            lauren.getLogger().info("[1/3] High important systems enabled successfully");

            lauren.onEnable();
            lauren.getLogger().info("[3/3] Registered Commands, Events, Timers and others");
        } catch (Exception exception) {
            exception.printStackTrace();
            lauren.shutdown();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            new Thread(lauren::shutdown).start();
        }

    }

}
