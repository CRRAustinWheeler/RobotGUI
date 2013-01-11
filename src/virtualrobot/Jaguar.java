/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

/**
 *
 * @author laptop
 */
class Jaguar {

    int id = ((int) (Math.random() * 20));

    double getBusVoltage() {
        return 12 + (Math.random() * 1);
    }

    double getOutputVoltage() {
        return 5 + (Math.random() * 3);
    }

    double getOutputCurrent() {
        return 10 + (Math.random() * 15);
    }

    double getTemperature() {
        return 40 + (Math.random() * 40);
    }

    int getID() {
        return id;
    }

    double getX() {
        return (Math.random() * 2) - 1.0;
    }
}
