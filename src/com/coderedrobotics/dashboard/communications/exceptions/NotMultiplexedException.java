package com.coderedrobotics.dashboard.communications.exceptions;

/**
 * Thrown when an object attempts to perform multiplexing actions on a Subsocket
 * that isn't multiplexed.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class NotMultiplexedException extends MultiplexException {

    public NotMultiplexedException() {
        super();
    }

    public NotMultiplexedException(String message) {
        super(message);
    }

    public NotMultiplexedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotMultiplexedException(Throwable cause) {
        super("That route doesn't support multiplexing.", cause);
    }

    @Override
    public String getMessage() {
        return "That route doesn't support multiplexing.";
    }
}
