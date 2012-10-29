/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

/**
 *
 * @author laptop
 */
public class FakeFeedback implements PIDOutput,PIDSource{
    
    double val = 0;

    public void pidWrite(double output) {
        val += output;
    }

    public double pidGet() {
        return val;
    }
    
}
