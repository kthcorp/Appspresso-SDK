package com.appspresso.waikiki.filesystem.errors;

import com.appspresso.api.AxError;

public class InvalidValuesError extends AxError {
    private static final long serialVersionUID = -3724852150883736697L;

    public InvalidValuesError(String message) {
        super(AxError.INVALID_VALUES_ERR, message);
    }
}
