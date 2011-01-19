package com.xebia.deconfluencer;

/**
 * An oversimplified Logger, since I'm just to darn lazy to configure SL4J.
 */
public class Logger {

    public void debug(String msg) {
        System.err.println(msg);
    }

    public void error(String msg, Throwable cause) {
        System.err.println(msg);
        cause.printStackTrace(System.err);
    }
}
