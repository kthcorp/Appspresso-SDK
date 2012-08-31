/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.error
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    /* predefined error codes */
    var errors = {
        // waikiki errors
        /** @name ax.UNKNOWN_ERR */
        0 : 'UNKNOWN_ERR',
        /** @name ax.INDEX_SIZE_ERR */
        1 : 'INDEX_SIZE_ERR',
        /** @name ax.DOMSTRING_SIZE_ERR */
        2 : 'DOMSTRING_SIZE_ERR',
        /** @name ax.HIERARCHY_REQUEST_ERR */
        3 : 'HIERARCHY_REQUEST_ERR',
        /** @name ax.WRONG_DOCUMENT_ERR */
        4 : 'WRONG_DOCUMENT_ERR',
        /** @name ax.INVALID_CHARACTER_ERR */
        5 : 'INVALID_CHARACTER_ERR',
        /** @name ax.NO_DATA_ALLOWED_ERR */
        6 : 'NO_DATA_ALLOWED_ERR',
        /** @name ax.NO_MODIFICATION_ALLOWED_ERR */
        7 : 'NO_MODIFICATION_ALLOWED_ERR',
        /** @name ax.NOT_FOUND_ERR */
        8 : 'NOT_FOUND_ERR',
        /** @name ax.NOT_SUPPORTED_ERR */
        9 : 'NOT_SUPPORTED_ERR',
        /** @name ax.INUSE_ATTRIBUTE_ERR */
        10 : 'INUSE_ATTRIBUTE_ERR',
        /** @name ax.INUSE_STATE_ERR */
        11 : 'INVALID_STATE_ERR',
        /** @name ax.SYNTAX_ERR */
        12 : 'SYNTAX_ERR',
        /** @name ax.INVALID_MODIFICATION_ERR */
        13 : 'INVALID_MODIFICATION_ERR',
        /** @name ax.NAMESPACE_ERR */
        14 : 'NAMESPACE_ERR',
        /** @name ax.INVALID_ACCESS_ERR */
        15 : 'INVALID_ACCESS_ERR',
        /** @name ax.VALIDATION_ERR */
        16 : 'VALIDATION_ERR',
        /** @name ax.TYPE_MISMATCH_ERR */
        17 : 'TYPE_MISMATCH_ERR',
        /** @name ax.SECURITY_ERR */
        18 : 'SECURITY_ERR',
        /** @name ax.NETWORK_ERR */
        19 : 'NETWORK_ERR',
        /** @name ax.ABORT_ERR */
        20 : 'ABORT_ERR',
        /** @name ax.TIMEOUT_ERR */
        21 : 'TIMEOUT_ERR',
        /** @name ax.INVALID_VALUES_ERR */
        22 : 'INVALID_VALUES_ERR',
        /** @name ax.NOT_AVAILABLE_ERR */
        24 : 'NOT_AVAILABLE_ERR',
        /** @name ax.IO_ERR */
        100 : 'IO_ERR',
        // custom errors
        /** @name ax.SYSTEM_ERR */
        1000 : 'SYSTEM_ERR',
        /** @name ax.UNEXPECTED_ERR */
        1001 : 'UNEXPECTED_ERR'
    };

    var DEF_CODE = 0;
    var DEF_MESSAGE = 'UNKNOWN_ERR';

    /**
     * this class represents an appspresso specific error object.
     *
     * use ax.error() to create an instance of this class.
     *
     * use ax.isError() to check an object is an instance of this class
     *
     * @param {number} code
     * @param {string} message
     * @param {Error} cause
     * @name AxError
     * @class
     * @constructor
     * @property {number} code
     * @property {string} message
     * @property {Error} cause
     * @see ax.error
     * @see ax.isError
     */
    function AxError(code, message, cause) {
        // 함수 오버로딩: function AxError(err) { ... }
        if(code instanceof Error) {
            message = code.message||DEF_MESSAGE;
            code = code.code||DEF_CODE;
        }

        if(!ax.isNumber(code)) { code = DEF_CODE; }
        if(!ax.isString(message)) { message = errors[code]||DEF_MESSAGE; }

        // 자바스크립트의 내장 Error 객체는 첫번째 인자로 message를 받음.
        // javascriptcore나 v8에는 fileName, lineNumber같은게 없는 거 같음... 있다고 한들 어쩌라고? -_-
        // https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Error
        Error.call(this, message);//Error.apply(this, arguments);

        this.name = 'AxError';

        this.__defineGetter__('code', function() {
            return code;
        });
        this.__defineGetter__('message', function() {
            return message;
        });
        this.__defineGetter__('cause', function() {
            return cause;
        });
        this.toString = function() {
            return [ this.name, ': ', message, ', code=[', code, ': ',
                    errors[code], '], cause=[', cause, ']' ].join('');
        };
    }
    AxError.constructor = Error;
    AxError.prototype = new Error;
    AxError.toString = function() { return 'function AxError() { [native code]  }'; };

    /**
     * factory method to create new "AxError" object.
     *
     * @param {number} code (optional. default: -1)
     * @param {string} message (optional. default: "<<error>>")
     * @param {Error} cause (optional. default: null)
     * @return {AxError}
     * @methodOf ax
     * @see AxError
     * @deprecated use <code>new ax.AxError(...)</code>
     */
    function error(code, message, cause) {
        return new AxError(code, message, cause);
    }

    /**
     * check whether "err" is "AxError" object.
     *
     * @param {object} err
     * @return {boolean}
     * @methodOf ax
     * @see AxError
     * @deprecated use <code>err instanceof ax.AxError</code>
     */
    function isError(err) {
        return err && (err instanceof AxError);
    }

    // define predefined error codes for both on class and object
    ax.util.foreach(errors, function(errCode, errMessage) {
        var code = Number(errCode);
        // access error code via error object(instance)
        ax.def(AxError.prototype).constant(errMessage, code);
        // access error code via ax.error(namespace)
        ax.def(error).constant(errMessage, code);
        // access error code via ax(global namespace)
        ax.def(ax).constant(errMessage, code);
    });

    ax.def(ax)
        .method('error', error)
        .method('isError', isError)
        .constant('AxError', AxError);
    // ====================================================
}(window));
