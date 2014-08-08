package com.coderedrobotics.dashboard.communications.exceptions;

/**
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class RouteException extends Exception {

    public RouteException() {
        super();
    }

    public RouteException(String message) {
        super(message);
    }

    public RouteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteException(Throwable cause) {
        super(cause);
    }
    
}
