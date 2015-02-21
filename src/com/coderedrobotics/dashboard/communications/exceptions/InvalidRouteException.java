package com.coderedrobotics.dashboard.communications.exceptions;

/**
 * Thrown when an object attempts to access a route that doesn't exist.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class InvalidRouteException extends RouteException {

    String message = "No subsocket exists at that route";
    
    public InvalidRouteException() {
        super();
    }

    public InvalidRouteException(String message) {
        super("No subsocket exists at that route: " + message);
        this.message = "No subsocket exists at that route: " + message;
    }

    public InvalidRouteException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRouteException(Throwable cause) {
        super("No subsocket exists at that route", cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
