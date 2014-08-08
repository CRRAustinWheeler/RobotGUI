package com.coderedrobotics.dashboard.communications;

import java.util.ArrayList;
import com.coderedrobotics.dashboard.communications.exceptions.RouteException;

/**
 * Manages the bindings of routes to unique IDs. IDs <b>cannot be reused</b>.
 * Since the bindings are stored in 32 bit numbers, the maximum amount of
 * available unique IDs is 4294967296, which sets the maximum amount of
 * Subsockets possible.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class IDFactory {

    private static final ArrayList<String> routes = new ArrayList<>();
    private static final ArrayList<Integer> ports = new ArrayList<>();
    private static int totalNumberOfUsedBindings = 0;

    private static final int ONE_BYTE_MAX = 127;
    private static final int TWO_BYTES_MAX = 32767;
    private static final int FOUR_BYTES_MAX = 2147483647;
    private static final int ONE_BYTE_MIN = -128;
    private static final int TWO_BYTES_MIN = -32768;
    private static final int FOUR_BYTES_MIN = -2147483648;

    private static int bytesRequiredToTransmit = 1;
    private static int currentMax = 127;
    private static int currentMin = -128;
    private static int currentIndex = -128;
    private static int lastMax = 300;// Initially, these values must be out of range
    private static int lastMin = -300; // Initially, these values must be out of range

    private static Connection connection;

    IDFactory(Connection connection) {
        IDFactory.connection = connection;
    }

    private static int getMaxSupported() {
        return currentMax + Math.abs(currentMin) + 1;
    }

    private synchronized static int addBinding(String route) {
        int index = currentIndex;
        ports.add(index);
        routes.add(route);
        totalNumberOfUsedBindings++;
        if (currentIndex == lastMin - 1) {
            currentIndex = lastMax + 1;
        } else {
            currentIndex++;
        }
        return index;
    }

    synchronized static void removeRoute(String route) {
        ports.remove(routes.indexOf(route));
        routes.remove(route);
    }

    /**
     * Returns the unique ID of a Subsocket.
     *
     * @param route the route to any Subsocket
     * @return the ID of the Subsocket at a given route.
     */
    public synchronized static int getID(String route) {
        if (routes.contains(route)) {
            return ports.get(routes.indexOf(route));
        } else if (totalNumberOfUsedBindings < getMaxSupported() - 1) {
            return addBinding(route);
        } else {
            fixMaxSupportedPorts();
            return addBinding(route);
        }
    }

    /**
     * Returns route to the Subsocket of a given ID.
     *
     * @param ID the unique ID of any Subsocket
     * @return the route to the Subsocket with the given ID..
     */
    public synchronized static String getRoute(int ID) throws RouteException {
        if (ports.contains(ID)) {
            return routes.get(ports.indexOf(ID));
        }
        throw new RouteException();
    }

    public static int getBytesRequiredToTransmit() {
        return bytesRequiredToTransmit;
    }

    private synchronized static void fixMaxSupportedPorts() {
        switch (bytesRequiredToTransmit) {
            case 1:
                bytesRequiredToTransmit = 2;
                currentMax = TWO_BYTES_MAX;
                currentMin = TWO_BYTES_MIN;
                currentIndex = currentMin;
                lastMax = ONE_BYTE_MAX;
                lastMin = ONE_BYTE_MIN;
                break;
            case 2:
                bytesRequiredToTransmit = 4;
                currentMax = FOUR_BYTES_MAX;
                currentMin = FOUR_BYTES_MIN;
                currentIndex = currentMin;
                lastMax = TWO_BYTES_MAX;
                lastMin = TWO_BYTES_MIN;
                break;
            case 4:
            //really?
        }
    }
}
