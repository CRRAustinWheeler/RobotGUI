/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import communications.SubsocketManager;

/**
 *
 * @author laptop
 */
public class RemoteVariable extends Primitive {
    
    private VariableModificationListener listener;

    public RemoteVariable(String tag, SubsocketManager manager) {
        super(tag, manager);
    }

    @Override
    protected String getExtCode() {
        return "var";
    }
    
    public void setListener(VariableModificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void pushData(byte[] b) {
        if (b.length == 1) {
            
        }
    }

    public interface VariableModificationListener {

        public void variableModified();
    }
}
