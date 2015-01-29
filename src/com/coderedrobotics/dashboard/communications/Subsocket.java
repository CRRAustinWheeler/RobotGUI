package com.coderedrobotics.dashboard.communications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.exceptions.RootRouteException;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;
import com.coderedrobotics.dashboard.dashboard.Debug;

/**
 * Subsocket Object for sending data.
 *
 * @see RootSubsocket
 * @see ControlSubsocket
 * @see InvalidRouteException
 * @see NotMutliplexedException
 * @see RootRouteException
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class Subsocket {

    private boolean multiplexed = false;
    private final String nodeName;
    private String fullRoute = "";
    private final ArrayList<Subsocket> subsockets;
    private final ArrayList<String> routes;
    private final Subsocket parent;
    private int ID;

    private final ArrayList<SubsocketListener> listeners;

    /**
     * Create a new Subsocket. This should only be done inside of the internal
     * network API.
     *
     * @param nodeName The name of the Subsocket.
     * @param parent The parent Subsocket.
     */
    Subsocket(String nodeName, Subsocket parent) {
        this(nodeName, parent, true, false);
    }

    Subsocket(String nodeName, Subsocket parent, boolean alert) {
        this(nodeName, parent, alert, false);
    }

    Subsocket(String nodeName, Subsocket parent, boolean alert, boolean manBind) {
        subsockets = new ArrayList<>();
        routes = new ArrayList<>();
        listeners = new ArrayList<>();
        this.nodeName = nodeName;
        this.parent = parent;
        if (!manBind) {
            BindingManager.handleNewSubsocket(this);
        }
        MultiplexingAlerts.alertRouteAdded(mapRoute(), alert);
        Debug.println("[NETWORK] NEW SUBSOCKET: Full Route: " + fullRoute
                + "\tNode name: " + nodeName + "\tID: " + ID, Debug.WARNING);
    }

    void setID(int ID) {
        Debug.println("[NETWORK] SUBSOCKET ID UPDATE: Full Route: " + fullRoute + "\tNEW ID: " + ID, Debug.WARNING);
        this.ID = ID;
    }

    /**
     * Enable multiplexing features on any Subsocket.
     *
     * @return the Subsocket that the method is called on.
     */
    public Subsocket enableMultiplexing() {
        multiplexed = true;
        MultiplexingAlerts.alertMultiplexingEnabled(mapRoute(), true);
        return this;
    }

    /**
     * Disable multiplexing features on any Subsocket. This will remove any
     * child Subsockets permanently, and alert MultiplexingListeners.
     *
     * @return the Subsocket that the method is called on.
     */
    public synchronized Subsocket disableMultiplexing() {
        multiplexed = false;
        subsockets.clear();
        routes.clear();
        MultiplexingAlerts.alertMultiplexingDisabled(mapRoute(), true);
        return this;
    }

    /**
     * Create a child Subsocket. All of the parents in the route must already
     * exist. Example: in the route <code>root.graph1.red.data</code>, the
     * <code>root</code>, <code>graph1</code>, and <code>red</code> Subsockets
     * must already exist. If they don't a InvalidRouteException is thrown.
     * Also, the route must start with the node name of the Subsocket that
     * createNewRoute() method is being called on. Example: if you are calling
     * createNewRoute() on th Subsocket with route <code>root.test</code>, then
     * you would use createNewRoute("test.newroutename")... not
     * createNewRoute("root.test.newroutename").
     *
     * @param route The route for the child Subsocket. Example:
     * <code>root.graph1.red</code>
     * @return the new Subsocket instance.
     * @throws NotMultiplexedException if the Subsocket is not multiplexed, and
     * thus cannot create a child Subsocket.
     * @throws InvalidRouteException if one or more parents for the new
     * Subsocket do not exist.
     */
    public synchronized Subsocket createNewRoute(String route) throws NotMultiplexedException, InvalidRouteException {
        String[] tree = route.split("\\.");
        Subsocket subsock = getSubsocket(getParentRoute(route));
        return subsock.createNewChild(tree[tree.length - 1], true);
    }

    /**
     * Enable multiplexing features on any Subsocket.
     *
     * @param alert Whether or not to alert the Connection thread.
     * @return the Subsocket that the method is called on.
     */
    Subsocket enableMultiplexing(boolean alert) {
        multiplexed = true;
        MultiplexingAlerts.alertMultiplexingEnabled(mapRoute(), alert);
        return this;
    }

    /**
     * Disable multiplexing features on any Subsocket. This will remove any
     * child Subsockets permanently, and alert MultiplexingListeners.
     *
     * @param alert Whether or not to alert the Connection thread
     * @return the Subsocket that the method is called on.
     */
    synchronized Subsocket disableMultiplexing(boolean alert) {
        multiplexed = false;
        subsockets.clear();
        routes.clear();
        MultiplexingAlerts.alertMultiplexingDisabled(mapRoute(), alert);
        return this;
    }

    /**
     * Create a child Subsocket. All of the parents in the route must already
     * exist. Example: in the route <code>root.graph1.red.data</code>, the
     * <code>root</code>, <code>graph1</code>, and <code>red</code> Subsockets
     * must already exist. If they don't a InvalidRouteException is thrown.
     * Also, the route must start with the node name of the Subsocket that
     * createNewRoute() method is being called on. Example: if you are calling
     * createNewRoute() on th Subsocket with route <code>root.test</code>, then
     * you would use createNewRoute("test.newroutename")... not
     * createNewRoute("root.test.newroutename").
     *
     * @param route The route for the child Subsocket. Example:
     * <code>root.graph1.red</code>
     * @param alert Whether or not to alert the Connection thread.
     * @return the new Subsocket instance.
     * @throws NotMultiplexedException if the Subsocket is not multiplexed, and
     * thus cannot create a child Subsocket.
     * @throws InvalidRouteException if one or more parents for the new
     * Subsocket do not exist.
     */
    synchronized Subsocket createNewRoute(String route, boolean alert) throws NotMultiplexedException, InvalidRouteException {
        String[] tree = route.split("\\.");
        Subsocket subsock = getSubsocket(getParentRoute(route));
        return subsock.createNewChild(tree[tree.length - 1], alert);
    }

    private synchronized Subsocket createNewChild(String nodeName, boolean alert) throws NotMultiplexedException {
        if (multiplexed) {
            if (routes.contains(nodeName)) {
                return subsockets.get(routes.indexOf(nodeName));
            }
            Subsocket child = new Subsocket(nodeName, this, alert);
            subsockets.add(child);
            routes.add(nodeName);
            return child;
        } else {
            throw new NotMultiplexedException();
        }
    }

    /**
     * Destroy any Subsocket that is a child of the one on which the method is
     * called.
     *
     * @param route A route to any Subsocket.
     * @throws InvalidRouteException if no Subsocket exists at that route.
     * @throws NotMultiplexedException if the Subsocket isn't multiplexed, and
     * therefore cannot delete any routes.
     * @throws RootRouteException if you attempt to delete the root Subsocket.
     */
    public synchronized void destroyRoute(String route) throws InvalidRouteException, NotMultiplexedException, RootRouteException {
        try {
            String[] tree = route.split("\\.");
            getSubsocket(route).getParent().deleteChild(tree[tree.length - 1], true);
        } catch (NullPointerException ex) {
            throw new RootRouteException(ex);
        }
    }

    /**
     * Destroy any Subsocket that is a child of the one on which the method is
     * called.
     *
     * @param route A route to any Subsocket.
     * @param alert Whether or not to alert the Connection thread.
     * @throws InvalidRouteException if no Subsocket exists at that route.
     * @throws NotMultiplexedException if the Subsocket isn't multiplexed, and
     * therefore cannot delete any routes.
     * @throws RootRouteException if you attempt to delete the root Subsocket.
     */
    synchronized void destroyRoute(String route, boolean alert) throws InvalidRouteException, NotMultiplexedException, RootRouteException {
        try {
            String[] tree = route.split("\\.");
            getSubsocket(route).getParent().deleteChild(tree[tree.length - 1], alert);
        } catch (NullPointerException ex) {
            throw new RootRouteException(ex);
        }
    }

    private synchronized void deleteChild(String child, boolean alert) throws NotMultiplexedException {
        if (multiplexed) {
            BindingManager.removeRoute(mapRoute() + "." + child);
            subsockets.remove(routes.indexOf(child));
            routes.remove(child);
            MultiplexingAlerts.alertRouteRemoved(mapRoute() + "." + child, alert);
        } else {
            throw new NotMultiplexedException();
        }
    }

    /**
     * Get any Subsocket that is a child of the one on which the method is
     * called.
     *
     * @param route A route to any Subsocket.
     * @return The Subsocket matching the given route.
     * @throws InvalidRouteException if no Subsocket exists at that route.
     */
    public Subsocket getSubsocket(String route) throws InvalidRouteException {
        return getSubsocket(route.split("\\."));
    }

    private Subsocket getSubsocket(String[] tree) throws InvalidRouteException {
        if (tree[0].equals(nodeName) && tree.length == 1) {
            return this;
        } else if (tree.length > 1) {
            synchronized (this) {
                int i = routes.indexOf(tree[1]);
                String[] modifiedTree = new String[tree.length - 1];
                System.arraycopy(tree, 1, modifiedTree, 0, modifiedTree.length);
                if (i > -1) {
                    return subsockets.get(i).getSubsocket(modifiedTree);
                }
            }
        }
        throw new InvalidRouteException(Arrays.toString(tree));
    }

    /**
     * Static method to remove the last node in a route. For example:
     * <code>root.hi.test</code> would become <code>root.hi</code>. The route
     * doesn't have to be valid.
     *
     * @param route Any route
     * @return the modified route
     */
    public static String getParentRoute(String route) {
        String[] tree = route.split("\\.");
        String[] modifiedTree = new String[tree.length - 1];
        System.arraycopy(tree, 0, modifiedTree, 0, tree.length - 1);
        String string = "";
        for (String s : modifiedTree) {
            string += s + ".";
        }
        return string.substring(0, string.length() - 1);
    }

    /**
     * Get the full route of a Subsocket.
     *
     * @return the full route to the Subsocket.
     */
    public String mapCompleteRoute() {
        return mapRoute();
    }

    private String mapRoute() {
        if ("".equals(fullRoute)) {
            Subsocket p = getParent();
            ArrayList<String> tree = new ArrayList<>();
            tree.add(nodeName);
            while (p != null) {
                tree.add(p.nodeName);
                p = p.getParent();
            }
            Collections.reverse(tree);
            String string = "";
            for (String s : tree) {
                string += s + ".";
            }
            fullRoute = string.substring(0, string.length() - 1);
            return fullRoute;
        } else {
            return fullRoute;
        }
    }

    /**
     * Get the parent of a Subsocket.
     *
     * @return The parent subsocket object, or null if the subsocket has no
     * parent.
     */
    public Subsocket getParent() {
        return parent;
    }

    /**
     * Get the ID of a Subsocket.
     *
     * @return The ID of the Subsocket, ranging from -2147483648 to 2147483647.
     */
    public int getID() {
        return ID;
    }

    void sendData(byte b) {
        getParent().sendData(b);
    }

    /**
     * Send data through the network through this subsocket.
     *
     * @param bytes The data to be sent
     */
    public void sendData(byte[] bytes) {
        if ("testsb".equals(nodeName)) {
            for (byte b : bytes) {
                Debug.println("SB WRITE: " + b, Debug.EXTENDED);
            }
        }
        switch (BindingManager.getBytesRequiredToTransmit()) {
            case 1:
                getParent().sendData((byte) ID);
                break;
            case 2:
                for (byte b : PrimitiveSerializer.toByteArray((short) ID)) {
                    getParent().sendData(b);
                }
                break;
            case 4:
                for (byte b : PrimitiveSerializer.toByteArray(ID)) {
                    getParent().sendData(b);
                }
                break;
        }
        for (byte b : PrimitiveSerializer.toByteArray((char) bytes.length)) {
            getParent().sendData(b);
        }
        for (byte b : bytes) {
            getParent().sendData(b);
        }
    }

    void pushData(byte[] data) {
        if ("testsb".equals(nodeName)) {
            for (byte b : data) {
                Debug.println("SB READ: " + b, Debug.EXTENDED);
            }
        }
        for (SubsocketListener listener : listeners) {
            listener.incomingData(data, this);
        }
    }

    /**
     * Objects that listen to a Subsocket receive announcements when data is
     * sent to that subsocket.
     *
     * @param listener An object implementing the {@link SubsocketListener}
     * interface.
     */
    public void addListener(SubsocketListener listener) {
        listeners.remove(listener);
        listeners.add(listener);
    }

    /**
     * Call this method to no longer receive announcements when data is sent to
     * that subsocket.
     *
     * @param listener An object implementing the {@link SubsocketListener}
     * interface.
     */
    public void removeListener(SubsocketListener listener) {
        listeners.remove(listener);
    }
}
