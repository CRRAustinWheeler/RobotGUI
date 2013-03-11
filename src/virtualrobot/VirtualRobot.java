/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author laptop
 */
public class VirtualRobot implements Runnable {

    static Thread thread;
    static FakeFeedback fakeFeedback;
    static DashBoard dashBoard;
    static PIDControllerAIAO pIDControllerAIAO;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        dashBoard = new DashBoard();
        dashBoard.monitorJaguar(new Jaguar());
        dashBoard.monitorJaguar(new Jaguar());
        fakeFeedback = new FakeFeedback();
        pIDControllerAIAO = new PIDControllerAIAO(0.01, 0, 0, fakeFeedback, fakeFeedback, dashBoard, "fake");
        pIDControllerAIAO.enable();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(VirtualRobot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dashBoard.prtln("Testing 1 2 3", 0);
                    dashBoard.prtln("l2(dbl1)", 1);
                    dashBoard.prtln("dbl2", 2);
                }
            }
        }).start();
    }

    public VirtualRobot() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        main(new String[0]);
    }
}
