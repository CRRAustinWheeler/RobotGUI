package com.coderedrobotics.dashboard.communications;

import com.coderedrobotics.dashboard.communications.MultiplexingManager.Type;

/**
 *
 * @author Michael
 */
class MPLXAction {

    private final MultiplexingManager.Type type;
    private final String route;
    private int port;

    MPLXAction(MultiplexingManager.Type type, String route) {
        this(type, route, 0);
    }

    MPLXAction(MultiplexingManager.Type type, String route, int port) {
        this.type = type;
        this.route = route;
        this.port = port;
    }

    boolean equals(MPLXAction action) {
        return action.type == type && action.route.equals(route);
    }

    MultiplexingManager.Type getType() {
        return type;
    }

    String getRoute() {
        return route;
    }

    int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
    }

    int typeToInt() {
        switch (type) {
            case MULTIPLEX_DISABLE:
                return 4;
            case MULTIPLEX_ENABLE:
                return 3;
            case ROUTE_ADD:
                return 1;
            case ROUTE_REMOVE:
                return 2;
            default:
                return 0;
        }
    }
    
    static Type intToType(int i) {
        switch(i) {
            case 1:
                return Type.ROUTE_ADD;
            case 2:
                return Type.ROUTE_REMOVE;
            case 3:
                return Type.MULTIPLEX_ENABLE;
            case 4:
                return Type.MULTIPLEX_DISABLE;
            default:
                return null;
        }
    }
}
