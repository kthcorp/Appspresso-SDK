/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.bridge
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    /**
     * native plugin을 호출하는 sync, async call의 parameter validation은 bridge에서 한다
     * (plugin, plugin.pluginName, method, params 등)
     *
     * bridge 이후에 호출되는 함수들에서는 validation을 하지 않는다
     *
     * 따라서 native plugin을 호출할 때는 반드시 bridge를 통해 호출할 것
     *
     * 주의, invalid parameter가 검출되면 AxError가 throw되므로 catch할 것
     *
     * @name ax.bridge
     * @namespace
     */
    var bridge = {};

    var watchID = 0;

    var watchSuccessListeners = {};

    var watchErrorListeners = {};

    var rpcID = 0;

    var SESSION = Date.now();

    var cbstore = ax.cbstore;

    // ----------------------------------------------------

    function validateCallback(scb, ecb) {
        if (scb && !ax.isFunction(scb)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid success callback');
        }
        if (ecb && !ax.isFunction(ecb)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid error callback');
        }
    }

    function validatePluginCall(method, params) {
        if (!ax.isString(method)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid method name');
        }
        if (!ax.isArray(params)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid parameter');
        }
    }

    function issueID() {
        return rpcID++;
    }

    /**
     * call native, async
     *
     * @param {string}
     *            method
     * @param {array}
     *            params
     * @return {*}
     * @methodOf ax.bridge
     */
    function execSync(method, params) {
        validatePluginCall(method, params);
        return ax.request(method, params, issueID()).doSync();
    }

    /**
     * call native, async
     *
     * @param {string}
     *            method
     * @param {function}
     *            successCallback
     * @param {function}
     *            errorCallback
     * @param {array}
     *            params
     * @param {number}
     *            [_id] debugging purpose
     * @return {AxRequst}
     * @methodOf ax.bridge
     */
    function execAsync(method, successCallback, errorCallback, params, _id) {
        validateCallback(successCallback, errorCallback);
        validatePluginCall(method, params);
        var id = _id || issueID(),
            req = ax.request(method, params, id, { async: true });
        cbstore.register(id, successCallback, errorCallback);
        req.doAsync(function oncancel() {
            return !!cbstore.pop(id);
        }, function onerror(err) {
            jsonrpc({ id: id, result: null, error: err });
        });
        return req;
    }

    /**
     * invoke success/error listener callback
     *
     * NOTE: might be invoked from native.
     *
     * @param {number}
     *            id
     * @param {array}
     *            param
     * @param {boolean}
     *            success or error
     * @methodOf ax.bridge
     */
    function invokeWatchCallback(id, param, success) {
        var result;
        var listeners = success ? watchSuccessListeners : watchErrorListeners;

        if (!listeners.hasOwnProperty(id)) {
            if(_DEBUG) {
                ax.debug('unknown watch id: {0}', id);
            }
            return;
        }

        if (success) {
            result = param.result;
        } else {
            if (param.error) {
                result = ax.error(param.error.code, param.error.message);
            } else {
                if(_DEBUG) {
                    ax.debug('invoke error listener: error is null or undefined');
                }
                result = ax.error(ax.UNKNOWN_ERR, 'unknown error');
            }
        }

        ax.util.invokeLater(null, listeners[id], result);
    }

    /**
     * @deprecated Since version 1.2. You should now use watch
     *
     * add watch listener
     *
     * @param {function}
     *            successCallback
     * @param {function}
     *            errorCallback
     * @return {number} id
     * @methodOf ax.bridge
     */
    function addWatchListener(successCallback, errorCallback) {
        validateCallback(successCallback, errorCallback);
        var id = ++watchID;
        watchSuccessListeners[id] = successCallback;
        watchErrorListeners[id] = errorCallback;
        return id;
    }

    /**
     * @deprecated Since version 1.2. You should now use stopWatch
     *
     * remove watch listener
     *
     * @param {number}
     *            id
     * @methodOf ax.bridge
     */
    function removeWatchListener(id) {
        if (watchSuccessListeners.hasOwnProperty(id)) {
            delete watchSuccessListeners[id];
        }

        if (watchErrorListeners.hasOwnProperty(id)) {
            delete watchErrorListeners[id];
        }
    }

    /**
     * @deprecated Since version 1.2. You should now use jsonrpc
     *
     * invoke watch success listener
     *
     * NOTE: might be invoked from native.
     *
     * @param {number}
     *            id
     * @param {array}
     *            param
     * @methodOf ax.bridge
     */
    function invokeWatchSuccessListener(id, param) {
        invokeWatchCallback(id, param, true);
    }

    /**
     * @deprecated Since version 1.2. You should now use jsonrpc
     *
     * invoke watch error listener
     *
     * NOTE: might be invoked from native.
     *
     * @param {number}
     *            id
     * @param {array}
     *            param
     * @methodOf ax.bridge
     */
    function invokeWatchErrorListener(id, param) {
        invokeWatchCallback(id, param, false);
    }

    /**
     * @deprecated Since version 1.2. You should now use watch
     *
     * add event noti listener
     *
     * @param {function}
     *            success <p>success listener</p>
     * @param {function}
     *            error <p>error listener</p>
     * @return {number} id
     * @methodOf ax.bridge
     */
    function addListener(success, error) {
        return addWatchListener(success, error);
    }

    /**
     * @deprecated Since version 1.2. You should now use stopWatch
     *
     * remove event noti listener
     *
     * @param {number}
     *            id
     * @methodOf ax.bridge
     */
    function removeListener(id) {
        removeWatchListener(id);
    }

    /**
     * @deprecated Since version 1.2. You should now use jsonrpc
     *
     * invoke event noti listener
     *
     * @param {number}
     *            id
     * @param {array}
     *            param
     * @methodOf ax.bridge
     */
    function invokeListener(id, param) {
        invokeWatchSuccessListener(id, param);
    }

    /**
     * reg watch func.
     *
     * @param {string} method
     * @param {function} successCallback
     * @param {function} errorCallback
     * @param {array} params
     * @return {number} watchID
     * @methodOf ax.bridge
     */
    function watch(method, successCallback, errorCallback, params) {
        validateCallback(successCallback, errorCallback);
        validatePluginCall(method, params);

        var watchID = issueID();
        cbstore.register(watchID, successCallback, errorCallback);

        ax.request(method, params, watchID, { watch: true, async: true }).doAsync();
        return watchID;
    }

    /**
     * stop watch and unreg watch func
     *
     * @param {string} method
     * @param {number} watchID
     * @methodOf ax.bridge
     */
    function stopWatch(method, watchID) {
        if (!ax.isString(method)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid method name');
        }
        if (!ax.isNumber(watchID)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid parameter');
        }

        cbstore.clear(watchID);
        ax.request(method, [watchID], issueID()).doSync();
    }

    /**
     * bridge session id getter
     *
     * @return {number} session id
     * @methodOf ax.bridge
     */
    function session() {
        return SESSION;
    }

    function callacallback(id, prop, param, keepcb) {
        var cbs = cbstore.peek(id);

        if (!cbs) {
            if (_DEBUG) {
                ax.debug('cannot find callbacks for jsonrpc response {0}: {1}',
                    id, ax.util.encodeJSON(param));
            }
            return;
        }

        if (!keepcb) {
            cbstore.clear(id);
        }

        var cb = cbs[prop]; // prop -> cb or eb

        try {
            cb && cb(param);
        } catch (e) {
            ax.log('uncaught exception from function {0}: {1}',
                ax.util.getFunctionName(cb),
                ax.util.encodeJSON(e));
        }
    }

    function onnotify(method, params) {
        if (method === 'ax.bridge.jsonrpc.plural') {
            params.forEach(function(rpc) {
                jsonrpc(rpc);
            });
        } else if (method === 'ax.bridge.eval') {
            var js = params[0];
            try {
                ax.util.evaluateJavaScript(js.replace(/;+$/, ''));
            } catch (e) {
                if (_DEBUG) {
                    ax.debug('uncaught exception during eval js "{0}": {1}', js, ax.util.encodeJSON(e));
                }
            }
        } else if (method === 'ax.watch.sample') {
            // one sample only ?
            var sample = params[0];

            if (sample.error !== null) {
                callacallback(sample.id, 'eb', ax.error(sample.error.code, sample.error.message), 'keep callback');
            } else {
                callacallback(sample.id, 'cb', sample.result, 'keep callback');
            }
        } else if (method === 'ax.event.trigger') {
            // one event only ?
            var evt = params[0];
            ax.event.trigger(evt.type, evt.params);
        } else {
            if (_DEBUG) {
                ax.debug("unrecognizable jsonrpc notification: { method: '{0}', params: {1} }", method, ax.util.encodeJSON(params));
            }
        }
    }

    /**
     * handle jsonrpc (response|notification)
     *
     * @param {string|object}
     *              json
     * @methodOf ax.bridge
     */
    function jsonrpc(json) {
        var obj;

        if (typeof json === 'string') {
            try {
                obj = ax.util.decodeJSON(json);
            } catch (e) {
                if (_DEBUG) {
                    ax.debug('bridge: unmarshaling error. abandon json - {0}', json);
                }
                return;
            }
        } else {
            obj = json;
        }

        if (obj.id === null) {
            onnotify(obj.method, obj.params);
        } else if (obj.error !== null) {
            callacallback(obj.id, 'eb', ax.error(obj.error.code, obj.error.message));
        } else {
            callacallback(obj.id, 'cb', obj.result);
        }
    }

    ax.def(bridge)
        .method('execSync', execSync)
        .method('execAsync', execAsync)
        .method('addWatchListener', addWatchListener)
        .method('removeWatchListener', removeWatchListener)
        .method('invokeWatchSuccessListener', invokeWatchSuccessListener)
        .method('invokeWatchErrorListener', invokeWatchErrorListener)
        .method('addListener', addListener)
        .method('removeListener', removeListener)
        .method('invokeListener', invokeListener)
        .method('watch', watch)
        .method('stopWatch', stopWatch)
        .method('session', session)
        .method('jsonrpc', jsonrpc);

    ax.def(ax).constant('bridge', bridge);
    // ====================================================
}(window));
