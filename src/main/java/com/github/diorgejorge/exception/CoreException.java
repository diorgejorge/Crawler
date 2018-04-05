package com.github.diorgejorge.exception;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Created by Diorge Jorge on 21/03/2018.
 */
public class CoreException extends Exception {
    private static final long serialVersionUID = 1L;

    private Logger logger = Logger.getLogger(CoreException.class);

    public CoreException() {
    }

    public CoreException(String message) {
        super("[Protocolo:" + (new Date()).getTime() + "] " + message);
    }

    public CoreException(Throwable cause) {
        super(cause);
        logger.error(cause);
    }

    public CoreException(String message, Throwable cause) {
        super("[Protocolo:" + (new Date()).getTime() + "] " + message, cause);
        logger.error(cause);
    }

    public CoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("[Protocolo:" + (new Date()).getTime() + "] " + message, cause, enableSuppression, writableStackTrace);
        logger.error(cause);
    }
}

