package com.yuhtin.lauren;

public class Startup {

    public static void main(String[] args) {

        Lauren lauren = new Lauren("Lauren");

        try {
            lauren.onLoad();
        } catch (Exception exception) {

            exception.printStackTrace();
            lauren.shutdown();

        }

        lauren.getLogger().info("[1/3] High important systems enabled successfully");

        try {
            lauren.onEnable();
        } catch (Exception exception) {

            exception.printStackTrace();
            lauren.shutdown();

        }

        lauren.getLogger().info("[2/3] Registered Commands, Events, Timers and others");


    }

}
