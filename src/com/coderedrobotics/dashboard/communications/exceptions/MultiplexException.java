package com.coderedrobotics.dashboard.communications.exceptions;

/**
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class MultiplexException extends Exception {

    public MultiplexException() {
        super();
    }

    public MultiplexException(String message) {
        super(message);
    }

    public MultiplexException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiplexException(Throwable cause) {
        super(cause);
    }
}
