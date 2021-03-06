package com.yuhtin.lauren.core.draw.controller;

import com.yuhtin.lauren.core.draw.Draw;

public class DrawController {

    public static DrawEditting editing;
    private static Draw current;

    public static Draw get() {
        return current;
    }

    public static void set(Draw draw) {
        current = draw;
        current.send();
    }

    public static void delete() {
        current = null;
    }
}
