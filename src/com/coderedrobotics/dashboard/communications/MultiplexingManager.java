package com.coderedrobotics.dashboard.communications;

import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.exceptions.RootRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.RouteException;
import com.coderedrobotics.dashboard.communications.listeners.MultiplexingListener;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Michael
 */
class MultiplexingManager implements SubsocketListener, MultiplexingListener {

    enum Type {

        ROUTE_ADD, ROUTE_REMOVE, MULTIPLEX_ENABLE, MULTIPLEX_DISABLE
    }

    public enum Mode {

        SYNC, RUN
    }
    Mode mode = Mode.SYNC;
    private final ControlSubsocket control;
    private final RootSubsocket root;
    private final ArrayListWrapper multiplexActions = new ArrayListWrapper();

    @SuppressWarnings("LeakingThisInConstructor")
    public MultiplexingManager(ControlSubsocket control, RootSubsocket root) {
        this.control = control;
        this.root = root;
        MultiplexingAlerts.addConnection(this);
    }

    @Override
    public void multiplexingEnabled(String route) {
        MPLXAction action = new MPLXAction(Type.MULTIPLEX_ENABLE, route);
        multiplexActions.add(action);
        control.sendMultiplexAction(action);
    }

    @Override
    public void multiplexingDisabled(String route) {
        MPLXAction action = new MPLXAction(Type.MULTIPLEX_DISABLE, route);
        multiplexActions.add(action);
        control.sendMultiplexAction(action);
    }

    @Override
    public void routeAdded(String route) {
        MPLXAction action = new MPLXAction(Type.ROUTE_ADD, route);
        multiplexActions.add(action);
        control.sendMultiplexAction(action);
    }

    @Override
    public void routeRemoved(String route) {
        MPLXAction action = new MPLXAction(Type.ROUTE_REMOVE, route);
        multiplexActions.add(action);
        control.sendMultiplexAction(action);
    }

    @Override
    public void incomingData(byte[] data, Subsocket subsocket) {
        if (data[0] == 5) {
            JOptionPane.showMessageDialog(null, "The robot side has sent a disconnect packet."
                    + "\nPlease restart the dash when the robot is\nback online to reconnect.",
                    "Robot disconnected", JOptionPane.WARNING_MESSAGE);
            Connection.getInstance().disconnect(); // writing the bye code will fail, so this forces reconnect();
            return;
        }
        
        if (mode == Mode.SYNC) {
            sync(data);
        } else {
            update(data);
        }
    }

    private void sync(byte[] data) {
        try {
            MPLXAction action = control.decodeByteArray(data);
            // System.out.println("SYNC CODE: " + action.typeToInt() + " on: " + action.getRoute());
            switch (action.getType()) {
                case MULTIPLEX_ENABLE:
                    root.getSubsocket(action.getRoute()).enableMultiplexing(false);
                    break;
                case MULTIPLEX_DISABLE:
                    root.getSubsocket(action.getRoute()).disableMultiplexing(false);
                    break;
                case ROUTE_ADD:
                    BindingManager.manualBind(action.getRoute(), action.getPort());
                    root.createNewRoute(action.getRoute(), false);
                    break;
                case ROUTE_REMOVE:
                    root.destroyRoute(action.getRoute(), false);
            }
        } catch (InvalidRouteException | NotMultiplexedException | RootRouteException ex) {
            Logger.getLogger(MultiplexingManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RouteException ex) {

        }
    }

    private void update(byte[] data) {
        try {
            MPLXAction action = control.decodeByteArray(data);
            switch (action.getType()) {
                case MULTIPLEX_ENABLE:
                    root.getSubsocket(action.getRoute()).enableMultiplexing(false);
                    break;
                case MULTIPLEX_DISABLE:
                    root.getSubsocket(action.getRoute()).disableMultiplexing(false);
                    break;
                case ROUTE_ADD:
                    root.createNewRoute(action.getRoute(), false);
                    break;
                case ROUTE_REMOVE:
                    root.destroyRoute(action.getRoute(), false);
            }
            // System.out.println("SYNC CODE: " + action.typeToInt() + " on: " + action.getRoute());
        } catch (InvalidRouteException | NotMultiplexedException | RootRouteException ex) {
            Logger.getLogger(MultiplexingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendMultiplexingActions() {
        for (MPLXAction action : multiplexActions.getArrayList()) {
            control.sendMultiplexAction(action);
        }
    }

    private class ArrayListWrapper {

        ArrayList<MPLXAction> data = new ArrayList<>();

        public void add(MPLXAction action) {
            if (contains(action)) {
                return;
            }
            data.add(action);
        }

        public void remove(MPLXAction action) {
            data.remove(action);
        }

        public void clear() {
            data.clear();
        }

        public ArrayList<MPLXAction> getArrayList() {
            ArrayList<MPLXAction> actions = new ArrayList<>();
            for (MPLXAction action : data) {
                actions.add(action);
            }
            return actions;
        }

        public boolean contains(MPLXAction action) {
            for (MPLXAction test : data) {
                if (test.equals(action)) {
                    return true;
                }
            }
            return false;
        }
    }
}
