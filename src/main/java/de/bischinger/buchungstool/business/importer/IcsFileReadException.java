package de.bischinger.buchungstool.business.importer;

/**
 * Created by Alexander Bischof on 31.07.15.
 */
public class IcsFileReadException extends RuntimeException {
    public IcsFileReadException() {
    }

    public IcsFileReadException(String message) {
        super(message);
    }

    public IcsFileReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public IcsFileReadException(Throwable cause) {
        super(cause);
    }

    public IcsFileReadException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
