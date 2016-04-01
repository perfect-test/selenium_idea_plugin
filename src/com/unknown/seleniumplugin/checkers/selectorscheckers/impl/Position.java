package com.unknown.seleniumplugin.checkers.selectorscheckers.impl;

/**
 * Created by mike-sid on 14.08.14.
 */
public class Position {
    private int position = 0;

    public int increment() {
        return ++position;
    }

    public void decrement() {
        position--;
    }

    public int value() {
        return position;
    }
}
