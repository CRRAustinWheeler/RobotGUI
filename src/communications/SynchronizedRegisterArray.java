/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import java.util.Vector;

/**
 *
 * @author laptop
 */
public class SynchronizedRegisterArray {

    private Vector registers;
    private Vector updateQueue;
    private Vector SRAListeners;

    public SynchronizedRegisterArray() {
        registers = new Vector();
        updateQueue = new Vector();
        SRAListeners = new Vector();
    }

    public synchronized void setRegister(String name, double val) {
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

    public synchronized void addSRAListener(SRAListener listener) {
        SRAListeners.add(listener);
        if (registers.size() != 0) {
            listener.alertToSRAUpdates();
        }
    }

    public synchronized boolean removeSRAListener(SRAListener listener) {
        return SRAListeners.remove(listener);
    }

    public synchronized Vector exchangeUpdates(Vector updates) {
        //Vector updatesToBePassedOn = new Vector();
        for (int i = 0; i < updates.size(); i++) {
            Register register = ((Register) updates.elementAt(i));
            if (indexOf(register, updateQueue) == -1) {
                //updatesToBePassedOn.add(register);
                int index = indexOf(register, registers);
                if (index != -1) {
                    ((Register) registers.elementAt(index)).val = register.val;
                } else {
                    registers.addElement(register);
                }
            }
        }
        for (int i = 0; i < SRAListeners.size(); i++) {
            SRAListener sral = (SRAListener)SRAListeners.get(i);
            sral.alertToSRAUpdates();
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
                    ((Register) registers.elementAt(i)).name,
                    ((Register) registers.elementAt(i)).val));
        }
    }

    public synchronized double get(String registerName) {
        int i = indexOf(new Register(registerName, 0), registers);
        if (i != -1) {
            return ((Register) registers.elementAt(i)).val;
        }
        return 0;
    }

    public synchronized String[] getRegisterNames() {
        String[] names = new String[registers.size()];
        for (int i = 0; i < registers.size(); i++) {
            names[i] = ((Register) registers.elementAt(i)).name;
        }
        return names;
    }
}