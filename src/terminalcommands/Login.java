/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package terminalcommands;

/**
 *
 * @author laptop
 */
public class Login implements Command,ContextCapturingCommand{

    @Override
    public String getContextName() {
        return "login";
    }
    
}
