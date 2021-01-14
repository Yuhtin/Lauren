package com.yuhtin.lauren.startup;

import com.yuhtin.lauren.Lauren;
import lombok.Getter;

import java.util.Scanner;

public class Startup {

    @Getter private static final Lauren lauren = new Lauren("Lauren");

    public static void main(String[] args) {

        try { lauren.onLoad(); } catch (Exception exception) {

            exception.printStackTrace();
            lauren.shutdown();
            return;

        }

        lauren.getLogger().info("[1/3] High important systems enabled successfully");

        try { lauren.onEnable(); } catch (Exception exception) {

            exception.printStackTrace();
            lauren.shutdown();
            return;

        }

        lauren.getLogger().info("[2/3] Registered Commands, Events, Timers and others");

        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            new Thread(lauren::shutdown).start();
        }

    }

}
