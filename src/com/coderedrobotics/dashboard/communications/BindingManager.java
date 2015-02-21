//package com.coderedrobotics.dashboard.communications;
//
//import java.util.ArrayList;
//import com.coderedrobotics.dashboard.communications.exceptions.RouteException;
//import com.coderedrobotics.dashboard.dashboard.Debug;
//
///**
// * Manages the bindings of routes to unique IDs. IDs <b>cannot be reused</b>.
// * Since the bindings are stored in 32 bit numbers, the maximum amount of
// * available unique IDs is 4294967296, which sets the maximum amount of
// * Subsockets possible.
// *
// * @author Michael Spoehr
// * @since Dash 3.0
// */
//public class BindingManager {
//
//    private static final ArrayList<String> routes = new ArrayList<>();
//    private static final ArrayList<Integer> ports = new ArrayList<>();
//    private static int totalNumberOfUsedBindings = 0;
//    private static final ArrayList<Subsocket> bindingQueue = new ArrayList<>();
//    private static final ArrayList<Integer> usedPortsInAvailableSpace = new ArrayList<>();
//
//    private static final int ONE_BYTE_MAX = 127;
//    private static final int TWO_BYTES_MAX = 32767;
//    private static final int FOUR_BYTES_MAX = 2147483647;
//    private static final int ONE_BYTE_MIN = -128;
//    private static final int TWO_BYTES_MIN = -32768;
//    private static final int FOUR_BYTES_MIN = -2147483648;
//
//    private static int bytesRequiredToTransmit = 1;
//    private static int currentMax = 127;
//    private static int currentMin = -128;
//    private static int currentIndex = -128;
//    private static int lastMax = 300;// Initially, these values must be out of range
//    private static int lastMin = -300; // Initially, these values must be out of range
//
//    private static Connection connection;
//    private static boolean stopQueueing = false;
//
//    BindingManager(Connection connection) {
//        BindingManager.connection = connection;
//    }
//    
//    static void printBindings() {
//        Debug.addChannel("BindingDebug", Debug.CRITICAL);
//        Debug.println("[BINDING] " + "TIMESTAMP: " + System.currentTimeMillis(), "BindingDebug");
//        for (String route : routes) {
//            Debug.println("[BINDING] " + route + " TO " + ports.get(routes.indexOf(route)), "BindingDebug");
//        }
//    }
//
//    private static int getMaxSupported() {
//        return currentMax + Math.abs(currentMin) + 1;
//    }
//
//    private synchronized static int addBinding(String route) {
//        int index = currentIndex;
//        ports.add(index);
//        routes.add(route);
//        totalNumberOfUsedBindings++;
//        if (currentIndex == lastMin - 1) {
//            currentIndex = lastMax + 1;
//        } else {
//            currentIndex++;
//        }
//        return index;
//    }
//
//    private synchronized static void addManualBinding(String route, int port) throws RouteException {
//        if (port == currentIndex) {
//            addBinding(route);
//            return;
//        } else if (!portIsInCurrentBlockSpace(port)) {
//            throw new RouteException("Port is not in current block space");
//        }
//        ports.add(port);
//        routes.add(route);
//        totalNumberOfUsedBindings++;
//    }
//
//    private static boolean portIsInCurrentBlockSpace(int port) {
//        switch (bytesRequiredToTransmit) {
//            case 1:
//                return port >= -128 && port < 128;
//            case 2:
//                return port >= -32768 && port < 32768;
//            case 4:
//                return port >= -2147483648 && port <= 2147483647;
//            default: // not possible, but java requires a default return value
//                return false;
//        }
//    }
//
//    private boolean bindingExists(String route) {
//        return routes.contains(route);
//    }
//
//    synchronized static void removeRoute(String route) {
//        if (routes.contains(route)) {
//            ports.remove(routes.indexOf(route));
//            routes.remove(route);
//        }
//        if (!bindingQueue.isEmpty()) {
//            for (Subsocket s : bindingQueue) {
//                if (s.mapCompleteRoute().equals(route)) {
//                    bindingQueue.remove(s);
//                }
//            }
//        }
//    }
//
//    synchronized static void queueForBinding(Subsocket subsocket) {
//        if (subsocket instanceof RootSubsocket) {
//            subsocket.setID(-128);
//            addBinding("root");
//        } else if (subsocket instanceof ControlSubsocket) {
//            subsocket.setID(-127);
//            addBinding("control");
//        }
//        bindingQueue.add(subsocket);
//    }
//
//    synchronized static void bindQueued() {
//        for (Subsocket s : bindingQueue) {
//            s.setID(getID(s.mapCompleteRoute()));
//        }
//        bindingQueue.clear();
//        stopQueueing = true;
//    }
//
//    synchronized static void manualBind(String route, int id) throws RouteException {
//        if (routes.contains(route) || ports.contains(id)) {
//            //throw new RouteException(); // if this actually ever happens, there is a big problem
//        } else if (totalNumberOfUsedBindings < getMaxSupported() - 1) {
//            addManualBinding(route, id);
//        } else {
//            fixMaxSupportedPorts();
//            addManualBinding(route, id);
//        }
//    }
//    
//    synchronized static void handleNewSubsocket(Subsocket s) {
//        if (!stopQueueing) {
//            queueForBinding(s);
//        } else {
//            s.setID(getID(s.mapCompleteRoute()));
//        }
//    }
//
//    /**
//     * Returns the unique ID of a Subsocket.
//     *
//     * @param route the route to any Subsocket
//     * @return the ID of the Subsocket at a given route.
//     */
//    public synchronized static int getID(String route) {
//        if (routes.contains(route)) {
//            return ports.get(routes.indexOf(route));
//        } else if (totalNumberOfUsedBindings < getMaxSupported() - 1) {
//            return addBinding(route);
//        } else {
//            fixMaxSupportedPorts();
//            return addBinding(route);
//        }
//    }
//    
//    /**
//     * Returns the ID of a Subsocket, if the binding exists.  This method will <B>not</b>
//     * create a new binding if one has not yet been created.
//     * 
//     * @param route the route to any (existing) Subsocket
//     * @return the ID of the Subsocket at the given route.
//     * @throws RouteException if the given route has not been bound to a port
//     */
//    public synchronized static int checkID(String route) throws RouteException {
//        if (routes.contains(route)) {
//            return ports.get(routes.indexOf(route));
//        }
//        throw new RouteException("Binding for " + route + " does not exist.");
//    }
//
//    /**
//     * Returns route to the Subsocket of a given ID.
//     *
//     * @param ID the unique ID of any Subsocket
//     * @return the route to the Subsocket with the given ID..
//     * @throws com.coderedrobotics.dashboard.communications.exceptions.RouteException if the binding doesn't exist.
//     */
//    public synchronized static String getRoute(int ID) throws RouteException {
//        if (ports.contains(ID)) {
//            return routes.get(ports.indexOf(ID));
//        }
//        throw new RouteException("Given ID: " + ID);
//    }
//
//    public static int getBytesRequiredToTransmit() {
//        return bytesRequiredToTransmit;
//    }
//
//    private synchronized static void fixMaxSupportedPorts() {
//        switch (bytesRequiredToTransmit) {
//            case 1:
//                bytesRequiredToTransmit = 2;
//                currentMax = TWO_BYTES_MAX;
//                currentMin = TWO_BYTES_MIN;
//                currentIndex = currentMin;
//                lastMax = ONE_BYTE_MAX;
//                lastMin = ONE_BYTE_MIN;
//                break;
//            case 2:
//                bytesRequiredToTransmit = 4;
//                currentMax = FOUR_BYTES_MAX;
//                currentMin = FOUR_BYTES_MIN;
//                currentIndex = currentMin;
//                lastMax = TWO_BYTES_MAX;
//                lastMin = TWO_BYTES_MIN;
//                break;
//            case 4:
//            //really?
//        }
//    }
//}
