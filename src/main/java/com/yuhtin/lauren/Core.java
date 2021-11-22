package com.yuhtin.lauren;

import com.yuhtin.lauren.core.LaurenCore;
import com.yuhtin.lauren.core.impl.LaurenInstance;

public class Core {

    private static final LaurenCore CORE = new LaurenInstance();

    public static void main(String[] args) {
        CORE.onEnable();

        Runtime.getRuntime().addShutdownHook(new Thread(Core::shutdownListener));
    }

    private static void shutdownListener() {
        CORE.onDisable();
    }

}
