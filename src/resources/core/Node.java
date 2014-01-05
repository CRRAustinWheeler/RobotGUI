/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.core;

import communications.SubsocketManager;
import communications.listeners.SubsocketListener;
import java.util.ArrayList;

/**
 *
 * @author laptop
 */
public class Node implements SubsocketListener{

    private SubsocketManager manager;
    private String path;
    private String[] childNodes;

    public Node(String tag, SubsocketManager manager) {
        this(null, tag, manager);
    }

    public Node(Node parrent, String tag, SubsocketManager manager) {
        String path;
        if (parrent != null) {
            path = parrent.getPath() + "/" + tag;
        } else {
            path = tag;
        }
        String ext = getExtCode();
        if (ext.matches("")) {
            this.path = path;
        } else {
            this.path = path + ":" + ext;
        }
    }

    protected String getExtCode() {
        return "";
    }

    public String getPath() {
        return path;
    }

    private ArrayList<String> populateChildNodes() {
        String[] tags = manager.getTags();
        ArrayList<String> result = new ArrayList<String>();
        int pathLength = path.length() + 1;
        for (String tag : tags) {
            if (tag.startsWith(path) && !path.substring(pathLength).contains("/")) {
                result.add(path.substring(pathLength));
            }
        }
        return result;
    }

    public synchronized String[] ls() {
        return childNodes.clone();
    }

    @Override
    public synchronized void SubsocketAdded(int subsocket) {
        populateChildNodes();
    }
}
