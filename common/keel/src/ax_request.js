/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.request
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    /*
     * endpoint http url(might be jsonrpc handler on kraken)
     *
     * NOTE: could be overridden via undocument global variable "_APPSPRESSO_REQUEST_URL"
     */
    var URL = '/appspresso/plugin';

    /*
     * endpoint http method(might be jsonrpc handler on kraken)
     *
     * NOTE: could be overridden via undocument global variable "_APPSPRESSO_REQUEST_METHOD"
     */
    var METHOD = 'POST';

    // ----------------------------------------------------

    /**
     * this class represents appspresso bridge request.
     *
     * use ax.request() to create an instance of this class.
     *
     * use ax.isRequest() to check an object is an instance of this class
     *
     * @param {number} id
     * @param {number} session bridge session key
     * @param {string} method
     * @param {array} params
     * @param {array} [requestParams]
     * @name AxRequest
     * @class
     * @constructor
     * @property {number} id
     * @property {string} method
     * @property {object} xhrArgs
     * @property {XMLHttpRequest} xhr
     * @see ax.request
     * @see ax.isRequest
     */
    function AxRequest(id, session, method, params, requestParams) {
        this.id = id;
        this.method = method;

        if(_DEBUG) {
            ax.debug('create AxRequest: id={0}, method={1}', id, method);
        }

        this.xhrArgs = {
            method : g._APPSPRESSO_REQUEST_METHOD||METHOD,
            async : true,
            url : g._APPSPRESSO_REQUEST_URL || ax.util.format('{0}/?id={1}&session={2}', URL, id, session),
            data : ax.util.encodeJSON({
                'id' : id,
                'method' : method,
                'params' : params || []
            })
        };

        if (requestParams) {
            for (var name in requestParams) {
                this.xhrArgs.url += ax.util.format('&{0}={1}', name, requestParams[name]);
            }
        }
    }

    function getNoResponseError(method) {
        if(_DEBUG) {
            ax.debug('{0} method did not return any value', method);
        }
        return ax.error(ax.UNKNOWN_ERR, 'plugin did not return any value');
    }

    function getRespondedError(tag, e) {
        if(_DEBUG) {
            ax.debug('{0}: xhrArgs.onload() response.error.message = {1}', tag,
                e.message);
        }
        return ax.error(e.code, e.message);
    }

    function getUndefinedResultError(tag) {
        if(_DEBUG) {
            ax.debug('{0}: xhrArgs.onload() response.result is undefined', tag);
        }
        return ax.error(ax.UNKNOWN_ERR, 'plug-in did not set result');
    }

    function getUnmarshallingError(tag, e) {
        if(_DEBUG) {
            ax.debug('{0}: xhrArgs.onload() caught error. err = {1}', tag, e);
        }
        return (ax.isError(e)) ? e : ax.error(ax.UNKNOWN_ERR,
                'an error ocurred while unmarshaling');
    }

    function getErrorOnError(tag, e) {
        if(_DEBUG) {
            ax.debug('{0}: xhrArgs.onerror() err = {1}', tag, e);
        }
        return ax.error(ax.UNKNOWN_ERR,
                'failed to call plugin or navigate away from a page');
    }

    /**
     * call native, sync.
     *
     * @methodOf AxRequest.prototype
     */
    function doSync() {
        var result = null, error = null;

        if(_DEBUG) {
            ax.debug('AxRequest.doSync()...url=' + this.xhrArgs.url);
        }
        this.xhrArgs.async = false;
        this.xhrArgs.onload = function(xhr) {
            try {
                if(_DEBUG) {
                    ax.debug('AxRequest.doSync() onload begin: xhr.responseText = {0}', xhr.responseText);
                }

                if (!xhr.responseText) {
                    error = getNoResponseError(this.method);
                } else {
                    var response = ax.util.decodeJSON(xhr.responseText);
                    if (response.error) {
                        error = getRespondedError('AxRequest.doSync()',
                                response.error);
                    } else {
                        if (response.result === undefined) {
                            error = getUndefinedResultError('AxRequest.doSync()');
                        } else {
                            if(_DEBUG) {
                                ax.debug('AxRequest.doSync() onload: response.result = {0}', response.result);
                            }
                            result = response.result;
                        }
                    }
                }
            } catch (e) {
                error = getUnmarshallingError('AxRequest.doSync()', e);
            }

            if(_DEBUG) {
                ax.debug('AxRequest.doSync(): onload end');
            }
        };
        this.xhrArgs.onerror = function(xhr, err) {
            error = getErrorOnError('AxRequest.doSync()', err);
        };

        ax.util.ajax(this.xhrArgs);

        if (error) {
            throw error;
        }

        return result;
    }

    /**
     * call native, async.
     *
     * @param {function} [oncancel]
     * @param {function} [onerror]
     * @param {function} [onload]  for debugging purpose
     *
     * @methodOf AxRequest.prototype
     */
    function doAsync(oncancel, onerror, onload) {
        this.oncancel = oncancel;

        if (_DEBUG) {
            ax.debug('AxRequest.doAsync()...');
        }

        // override async flag!
        this.xhrArgs.async = true;

        this.xhrArgs.onload = function(xhr) {
            // ajax 성공 했다면 결과값은 longpoll 혹은 loadUrl 로 받기 때문에 무시하면 된다.
            // 테스트를 위해 onload callback 이 주어졌을 때에만 알려준다.
            onload && onload();
        };
        this.xhrArgs.onerror = function(xhr) {
            onerror && onerror({ code: xhr.status, message: xhr.statusText });
        };

        ax.util.ajax(this.xhrArgs);
        return this;
    }

    /**
     * cancel async call.
     *
     * @methodOf AxRequest.prototype
     */
    function cancel() {
        var canceled = this.oncancel ? this.oncancel(this.id) : false;

        if (_DEBUG) {
            canceled ?
                ax.debug('AxRequest.cancel(): Asynchronous operation has been aborted') :
                ax.debug('AxRequest.cancel(): No callbacks to cancel. req id = {0}', this.id);
        }
        return canceled;
    }

    // ----------------------------------------------------

    ax.def(AxRequest.prototype)
        .method('doSync', doSync)
        .method('doAsync', doAsync)
        .method('cancel', cancel);

    // ----------------------------------------------------

    /**
     * factory methtod to create new "AxRequest" object.
     *
     * @param {string}
     *            method
     * @param {array}
     *            params
     * @param {number}
     *            id
     * @param {string}
     *            [requestParams]
     * @return {AxRequest}
     * @methodOf ax
     * @see AxRequest
     */
    function request(method, params, id, requestParams) {
        return new AxRequest(id, ax.bridge.session(), method, params, requestParams);
    }

    /**
     * check whether "req" is "AxRequest" object.
     *
     * @param {AxRequest}
     *            req
     * @return {boolean}
     * @methodOf ax
     * @see AxRequest
     */
    function isRequest(req) {
        return req && (req instanceof AxRequest);
    }

    // ----------------------------------------------------

    ax.def(ax)
        .method('request', request)
        .method('isRequest', isRequest);
    // ====================================================
}(window));
