package com.yuhtin.lauren.util;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvWrapper {

    private static Dotenv env;

    public static String get(String key) {
        if (env == null) env = Dotenv.load();
        return env.get(key);
    }

    public static boolean isDebugMode() {
        if (env == null) env = Dotenv.load();
        return !env.get("DEBUG_MODE", "false").equalsIgnoreCase("false");
    }
}
