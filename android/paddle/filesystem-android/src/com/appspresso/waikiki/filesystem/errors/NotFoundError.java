package com.appspresso.waikiki.filesystem.errors;

import com.appspresso.api.AxError;

public class NotFoundError extends AxError {
    private static final long serialVersionUID = -6529015275273890276L;

    public NotFoundError(String message) {
        super(AxError.NOT_FOUND_ERR, message);
    }
}
