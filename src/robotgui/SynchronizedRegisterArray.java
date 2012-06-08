/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import java.util.Vector;

/**
 *
 * @author laptop
 */
public class SynchronizedRegisterArray {

    private Vector registers;
    private Vector updateQueue;

    public SynchronizedRegisterArray() {
        registers = new Vector();
        updateQueue = new Vector();
    }

    public synchronized void setRegister(String name, int val) {
        Register register = new Register(name, val);
        int index = indexOf(register, registers);
        if (index != -1) {
            if (((Register) registers.elementAt(index)).val == register.val) {
                return;
            }
            ((Register) registers.elementAt(index)).val = register.val;
        } else {
            registers.addElement(register);
        }
        index = indexOf(register, updateQueue);
        if (index != -1) {
            if (((Register) updateQueue.elementAt(index)).val == register.val) {
                System.out.println("Error");
                System.exit(1);
            }
            ((Register) updateQueue.elementAt(index)).val = register.val;
        } else {
            updateQueue.addElement(register);
        }
    }

    public synchronized Vector updateExchange(Vector updates) {
        for (int i = 0; i < updates.size(); i++) {
            Register register = ((Register) updates.elementAt(i));
            if (indexOf(register, updateQueue) == -1) {
                int index = indexOf(register, registers);
                if (index != -1) {
                    ((Register) registers.elementAt(index)).val = register.val;
                } else {
                    registers.addElement(register);
                }
            }
        }
        Vector updateQueue = this.updateQueue;
        this.updateQueue = new Vector();
        return updateQueue;
    }

    private int indexOf(Register register, Vector list) {
        for (int i = 0; i < list.size(); i++) {
            if (((Register) list.elementAt(i)).name.equals(register.name)) {
                return i;
            }
        }
        return -1;
    }

    private class Register {

        String name;
        double val;

        public Register(String name, double val) {
            this.name = name;
            this.val = val;
        }
    }
}