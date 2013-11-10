/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

/**
 *
 * @author laptop
 */
public class Folder {

    private String path;

    public Folder(String path) {
        String ext = getExtCode();
        if (ext.matches("")) {
            this.path = path;
        } else {
            this.path = path + "." + ext;
        }
    }

    protected String getExtCode() {
        return "";
    }

    public String getPath() {
        return path;
    }
}
