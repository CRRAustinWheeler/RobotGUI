/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.core;

/**
 *
 * @author laptop
 */
public class Node {

    private String path;

    public Node(String tag) {
        this(null, tag);
    }

    public Node(Node parrent, String tag) {
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
}
