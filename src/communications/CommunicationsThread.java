/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import java.util.Vector;

/**
 *
 * @author laptop
 */
public class CommunicationsThread implements Runnable {

    Thread thread;
    SimpleSock sock;
    private String[] tags = new String[32767];

    public CommunicationsThread(SimpleSock sock) {
        this.sock = sock;
        thread = new Thread(this);
        thread.start();
    }

    int lookUpString(String tag) {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].matches(tag)) {
                return i;
            }
        }
        return -1;
    }

    void sendData(int i, byte[] bytes) {
        
    }

    public void run() {
        while (true) {
            try {
                sendRegisterArrayUpdates(sock,
                        synchronizedRegisterArray.exchangeUpdates(
                        new Vector()));
                sendStreamUpdates(sock,
                        dataStreamingModule.exchangeUpdates(
                        new Vector()));
                sock.flush();

                while (true) {
                    Vector arrayUpdates = getRegisterArrayUpdates(sock);
                    Vector streamUpdates = getStreamUpdates(sock);
                    arrayUpdates = synchronizedRegisterArray.
                            exchangeUpdates(arrayUpdates);
                    streamUpdates = dataStreamingModule.
                            exchangeUpdates(streamUpdates);
                    sendRegisterArrayUpdates(sock, arrayUpdates);
                    sendStreamUpdates(sock, streamUpdates);
                    sock.flush();
                }
            } catch (ConnectionResetException cre) {
                synchronizedRegisterArray.resynchronize();
            }
        }
    }
}
