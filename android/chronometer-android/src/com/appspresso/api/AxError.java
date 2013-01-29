/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api;

/**
 * This class represents an plugin method execution error for appspresso runtime.
 * 
 * All predefined errors are based-on DeviceAPIError in Waikiki API.
 * 
 * When you need custom error codes,
 * 
 * Do *NOT* use magic number like this:
 * 
 * <code>
 * throw new AxError(123);
 * </code>
 * 
 * Do use {@link #toErrorCode(String)} to generate an unqiue error code:
 * 
 * <code>
 * private static final int CUSTOM_ERR = AxError.toErrorCode("Custom Error");
 * ...
 * throw new AxError(CUSTOM_ERR);
 * </code>
 * 
 * NOTE: Do *NOT* throw this exception out-of a plugin context.
 * 
 */
public class AxError extends AxException {

    private static final long serialVersionUID = 1L;

    //
    // deviceapis
    //

    public static final int UNKNOWN_ERR = 0;

    public static final int INDEX_SIZE_ERR = 1;
    public static final int DOMSTRING_SIZE_ERR = 2;
    public static final int HIERARCHY_REQUEST_ERR = 3;
    public static final int WRONG_DOCUMENT_ERR = 4;
    public static final int INVALID_CHARACTER_ERR = 5;
    public static final int NO_DATA_ALLOWED_ERR = 6;
    public static final int NO_MODIFICATION_ALLOWED_ERR = 7;
    public static final int NOT_FOUND_ERR = 8;
    public static final int NOT_SUPPORTED_ERR = 9;

    public static final int INUSE_ATTRIBUTE_ERR = 10;
    public static final int INVALID_STATE_ERR = 11;
    public static final int SYNTAX_ERR = 12;
    public static final int INVALID_MODIFICATION_ERR = 13;
    public static final int NAMESPACE_ERR = 14;
    public static final int INVALID_ACCESS_ERR = 15;
    public static final int VALIDATION_ERR = 16;
    public static final int TYPE_MISMATCH_ERR = 17;
    public static final int SECURITY_ERR = 18;
    public static final int NETWORK_ERR = 19;
    public static final int ABORT_ERR = 20;
    public static final int TIMEOUT_ERR = 21;
    public static final int INVALID_VALUES_ERR = 22;

    //
    // filesystem
    //

    public static final int IO_ERR = 100;

    //
    // devicestatus
    //

    public static final int NOT_AVAILABLE_ERR = 101;

    // ... more predefined error codes here...
    // public static final int FOO_ERR = ???;

    /**
     * all custom error codes are bigger than this value.
     */
    private static final int CUSTOM_ERR_FLAG = 0x10000;

    /**
     * get a unique error code for the given error identifier.
     * 
     * @param id error identifier
     * @return unique error code
     */
    public static int toErrorCode(String id) {
        return CUSTOM_ERR_FLAG | id.intern().hashCode();
    }

    //
    //
    //

    private final int code;

    public AxError(int code, String message) {
        super(message);
        this.code = code;
    }

    public AxError(int code) {
        this(code, "");
    }

    public AxError(String id, String message) {
        this(toErrorCode(id), message);
    }

    public AxError(String id) {
        this(toErrorCode(id), id);
    }

    //
    //
    //

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return new StringBuilder(200).append("AxError#").append(code).append(':')
                .append(super.toString()).toString();
    }

}
