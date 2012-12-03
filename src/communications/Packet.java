/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

/**
 *
 * @author laptop
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
