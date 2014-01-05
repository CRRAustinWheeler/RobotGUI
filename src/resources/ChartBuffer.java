/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package resources;

import resources.core.GenericListener;
import resources.core.Node;

/**
 *
 * @author austin
 */
public class ChartBuffer implements GenericListener{
    private double[] data;

    public ChartBuffer(int length) {
        data = new double[length];
    }
    
    @Override
    public void pushData(Node node, Object data) {
        
    }
}
