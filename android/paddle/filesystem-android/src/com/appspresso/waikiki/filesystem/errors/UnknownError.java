package com.appspresso.waikiki.filesystem.errors;

import com.appspresso.api.AxError;

public class UnknownError extends AxError {
    private static final long serialVersionUID = 7530674947378694891L;

    public UnknownError(String message) {
        super(AxError.UNKNOWN_ERR, message);
    }
}
