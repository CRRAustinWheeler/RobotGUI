/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package terminalcommands;

/**
 *
 * @author laptop
 */
interface Command {
    public String executeCommand(String command);
    public String getName();
}
