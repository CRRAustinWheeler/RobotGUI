/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import communications.listeners.DataListener;

/**
 *
 * @author laptop
 */
public class Subsocket {

    SubsocketManager manager;
    private int index;
    public DataListener dataListener;

    public Subsocket(SubsocketManager manager, int index) {
        this.manager = manager;
        this.index = index;
    }

    public void sendData(byte[] b) {
        manager.sendData(index, b);
    }
}
