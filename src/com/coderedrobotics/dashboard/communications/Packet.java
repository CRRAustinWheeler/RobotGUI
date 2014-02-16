package com.coderedrobotics.dashboard.communications;

/**
 *
 * @author Austin
 */
public class Packet {

    public final double val;
    public final String name;
    public final long time;

    Packet(double val, String name, long time) {
        this.val = val;
        this.name = name;
        this.time = time;
    }
}
