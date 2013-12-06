package resources;

import resources.core.Node;

public class RemoteVariable extends Node {

    private VariableModificationListener listener;
    private SynchronizedDouble var, overide;

    public RemoteVariable(Node parrent, String tag) {
        super(parrent, tag);
    }

    @Override
    protected String getExtCode() {
        return "var";
    }

    public void setListener(VariableModificationListener listener) {
        this.listener = listener;
    }

    public interface VariableModificationListener {

        public void variableModified();
    }
}
