package de.bischinger.buchungstool.business;

/**
 * Created by Alexander Bischof on 31.07.15.
 */
public class IllegalTimeException extends RuntimeException {
    public IllegalTimeException() {
    }

    public IllegalTimeException(String message) {
        super(message);
    }

    public IllegalTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTimeException(Throwable cause) {
        super(cause);
    }

    public IllegalTimeException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
