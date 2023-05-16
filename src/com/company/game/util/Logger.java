package com.company.game.util;

public class Logger {
    public static void log(String message) {
        System.out.println("\u001B[1m\u001B[31mLOG: " + message + "\u001B[0m");
    }
}
