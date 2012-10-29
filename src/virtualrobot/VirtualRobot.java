/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

/**
 *
 * @author laptop
 */
public class VirtualRobot implements Runnable{
    
    static Thread thread;
    static FakeFeedback fakeFeedback;
    static DashBoard dashBoard;
    static PIDControllerAIAO pIDControllerAIAO;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        dashBoard = new DashBoard();
        fakeFeedback = new FakeFeedback();
        pIDControllerAIAO = new PIDControllerAIAO(0.01, 0, 0, fakeFeedback, fakeFeedback, dashBoard, "fake");
        pIDControllerAIAO.enable();
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
