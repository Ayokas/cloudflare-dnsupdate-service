package org.ayokas.cloudflarednsupdateservice;

public class CloudflareErrorException extends Exception {
    public CloudflareErrorException() {
    }

    public CloudflareErrorException(String message) {
        super(message);
    }

    public CloudflareErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudflareErrorException(Throwable cause) {
        super(cause);
    }

    public CloudflareErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
