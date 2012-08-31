/*
 * Appspresso
 * 
 * Copyright (c) 2011 KT Hitel Corp.
 * 
 * This source is subject to Appspresso license terms. Please see http://appspresso.com/ for more
 * information.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE.
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
