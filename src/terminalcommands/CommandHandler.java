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

    public CommandHandler() {
        commands = new ArrayList();
        commands.add(new Login());
    }

    public String executeCommand(String command) {
        String message = "";
        for (Command com : commands) {
            if (com.getName().matches(getFirstWord(command))) {
                message = com.executeCommand(removeFirstWord(command));
            }
        }
        return message;
    }

    public static String getFirstWord(String command) {
        if (command.indexOf(" ") != -1) {
            return command.substring(0, command.indexOf(" "));
        }
        return command;
    }

    public static String removeFirstWord(String command) {
        if (command.indexOf(" ") != -1) {
            return command.substring(command.indexOf(" ") + 1);
        }
        return "";
    }
}
