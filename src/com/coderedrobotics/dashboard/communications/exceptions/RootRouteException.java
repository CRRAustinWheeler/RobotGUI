package com.coderedrobotics.dashboard.communications.exceptions;

/**
 * Thrown when an object attempts to perform an action on the RootSubsocket that
 * cannot be performed.  This includes actions like deleting the RootSubsocket.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class RootRouteException extends RouteException {

    public RootRouteException() {
        super();
    }

    public RootRouteException(String message) {
        super(message);
    }

    public RootRouteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RootRouteException(Throwable cause) {
        super("Cannot perform that action on root route", cause);
    }

    @Override
    public String getMessage() {
        return "Cannot perform that action on root route";
    }
}
