/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package share;

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

    public synchronized Vector exchangeUpdates(Vector updates) {
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

    public synchronized void resynchronize() {
        updateQueue = new Vector(registers.size());
        for (int i = 0; i < registers.size(); i++) {
            updateQueue.addElement(new Register(
                    ((Register)registers.elementAt(i)).name,
                    ((Register)registers.elementAt(i)).val));
        }
    }
}