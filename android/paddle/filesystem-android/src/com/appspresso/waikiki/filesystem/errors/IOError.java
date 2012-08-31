package com.appspresso.waikiki.filesystem.errors;

import com.appspresso.api.AxError;

public class IOError extends AxError {
    private static final long serialVersionUID = 3975743032880608397L;

    public IOError(String message) {
        super(AxError.IO_ERR, message);
    }
}
