/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package terminalcommands;

import java.util.ArrayList;

/**
 *
 * @author laptop
 */
public class CommandHandler {

    private ArrayList<Command> commands;

    public CommandHandler(ArrayList<Command> commands) {
        this.commands = commands;
    }

    public String executeCommand(String command) {

        return "";
    }

    public static String getFirstWord(String command) {
        return command.substring(0, command.indexOf(" "));
    }

    public static String removeFirstWord(String command) {
        return command.substring(command.indexOf(" ") + 1);
    }
}
