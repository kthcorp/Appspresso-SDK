/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api;

/**
 * This class represents a generic exception for whole appspresso runtime.
 * 
 * You *SHOULD* extend this exception to represent more specific errors.
 * 
 */
public abstract class AxException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AxException() {
        super();
    }

    public AxException(String detailMessage) {
        super(detailMessage);
    }

    public AxException(Throwable throwable) {
        super(throwable);
    }

    public AxException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
