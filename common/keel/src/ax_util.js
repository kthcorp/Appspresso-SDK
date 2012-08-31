/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.util
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    /**
     * utility functions for appspresso javascript API.
     *
     * @name ax.util
     * @namespace utility functions for appspresso javascript API.
     */
    var util = {};

    // ----------------------------------------------------

    /**
     * "safe-guard" for evil "eval". call this method instead of the evil.
     *
     * @param {string} script
     * @return result
     * @methodOf ax.util
     */
    function evaluateJavaScript(script) {
        // TODO: check injection attack
        if (script === '') {
            return;
        }
        /*jslint evil:true*/
        var ret = eval('(' + script + ')');
        /*jslint evil:false*/
        return ret;
    }

    /**
     * "safe-guard" for JSON.parse().
     *
     * @param {string} json
     * @return {object}
     * @methodOf ax.util
     */
    function decodeJSON(json) {
        if (json === undefined || json === null || json === '') {
            ax.debug('[W] decodeJSON(): json is null/undefined/empty');
            return {};
        }
        // TODO: check injection attack
        try {
            // return JSON.parse(json);
            return evaluateJavaScript(json);
        } catch (e) {
            ax.debug('[E] decodeJSON(): eval error {0}', e);
            throw ax.error(ax.INVALID_VALUES_ERR, e);
        }
    }

    /**
     * "safe-guard" for JSON.stringify().
     *
     * @param {object} obj
     * @return {string} json representation for the "obj"
     * @methodOf ax.util
     */
    function encodeJSON(obj) {
        return JSON.stringify(obj);
    }

    /**
     * "safe-guard" for webkit's built-in DOMParser.
     *
     * @param {string} xml
     * @return {XMLDocument}
     * @methodOf ax.util
     */
    function parseXML(xml) {
        return new DOMParser().parseFromString(xml, 'text/xml');
    }

    /**
     * iterate all elements in object, array or array-like object.
     *
     * @param {object} obj
     * @param {function} callback
     * @return {object}
     * @methodOf ax.util
     */
    function foreach(obj, callback) {
        if (!obj) {
            return null;
        }
        if (!callback) {
            return null;
        }
        var k, v;
        if(ax.isArrayLike(obj)) {
            for(k = 0; k < obj.length; k++) {
                v = obj[k];
                if(callback(k, v)) {
                    return k; // stop iteration when callback returns 'true'
                }
            }
        } else {
            for (k in obj) {
                if (obj.hasOwnProperty(k)) {
                    v = obj[k];
                    if(callback(k, v)) {
                        return k; // stop iteration when callback returns 'true'
                    }
                }
            }
        }
        return null;
    }

    var DUMP_MAX_DEPTH = 3;
    var DUMP_INDENT = '\t';
    var DUMP_NEWLINE = '\n';
    var DUMP_UNDEFINED = '<<undefined>>';
    var DUMP_ARRAY = '<<array>>';
    var DUMP_ARRAY_LIKE = '<<array-like>>';
    var DUMP_FUNCTION = '<<function>>';
    var DUMP_OBJECT = '<<object>>';
    var DUMP_NULL = '<<null>>';

    /**
     * make string representation from object. used for debugging.
     *
     * @param {object} obj
     * @param {number} depth (optional; default: 3)
     * @param {string} indent (optional; default: '\t')
     * @param {string} newline (optional; default: '\n')
     * @param {string} separator (optional; default: ',' + newline + indent)
     * @return {string}
     * @methodOf ax.util
     */
    function dump(obj, depth, indent, newline, separator) {
        if (typeof obj === 'undefined') {
            return DUMP_UNDEFINED;
        }
        if (obj === null) {
            return DUMP_NULL;
        }
        // primitive types
        if (typeof obj === 'string') {
            return [ '\"', obj, '\"'].join('');
        }
        if (typeof obj === 'number' || typeof obj === 'number') {
            return String(obj);
        }
        // complex types
        var props = [];
        if (!ax.isNumber(depth)) {
            depth = DUMP_MAX_DEPTH;
        }
        if (!ax.isString(indent)) {
            indent = DUMP_INDENT;
        }
        if (!ax.isString(newline)) {
            newline = DUMP_NEWLINE;
        }
        if (!ax.isString(separator)) {
            separator = [',', newline, indent].join('');
        }
        if (ax.isArrayLike(obj)) {
            if (depth <= 0) {
                if (ax.isArray(obj)) {
                    return DUMP_ARRAY;
                }
                return DUMP_ARRAY_LIKE;
            }
            foreach(obj, function(k, v) {
                // NOTE: recursion...
                props.push(dump(v, depth - 1, indent + indent));
            });
            return [ '[', newline, indent, props.join(separator), newline, ']' ].join('');
        }
        if (typeof obj === 'function') {
            return DUMP_FUNCTION;
        }
        if (typeof obj === 'object') {
            if (depth <= 0) {
                return DUMP_OBJECT;
            }
            foreach(obj, function(k, v) {
                // NOTE: recursion...
                props.push([ k, dump(v, depth - 1, indent + indent) ].join(':'));
            });
            return [ '{', newline, indent, props.join(separator), newline, '}' ].join('');
        }
        return obj.toString();
    }

    var AJAX_RETRY_DEFAULT = 3;

    /**
     * performs an ajax request.
     *
     * @param {object}
     *            args
     * @return {object} XMLHttpRequest
     * @methodOf ax.util
     */
    function ajax(args) {
        // TODO: alternative transport
        var xhr = new XMLHttpRequest(),
            debug_time,
            retry = args.retry !== undefined ? args.retry : (getConfig('ajaxRetry') || AJAX_RETRY_DEFAULT);

        xhr.onreadystatechange = function() {
            if(_DEBUG) {
                //ax.debug('ajax xhr.onreadystatechange: xhr.readyState=' + xhr.readyState);
            }
            if (xhr.readyState === 4) {
                if(_DEBUG) {
                    ax.debug('ajax xhr.onreadystatechange: xhr.status={0}, elapse={1}ms', xhr.status, (new Date().getTime() - debug_time));
                }

                if(xhr.status === 0) {
                    // non http request
                    // http://stackoverflow.com/questions/2801571
                    if((g.location.protocol === 'file:') && (args.url.indexOf('://') < 0)) {
                        return args.onload(xhr);
                    }
                    // sometime, *android* apache http core closes ajax connection without any explanation...
                    if (retry) {
                        ax.debug('##### AJAX RETRY!!! ##### - ' + retry);
                        args.retry = --retry;
                        return ajax(args);
                    }
                }

                if (xhr.status >= 200 && xhr.status < 300) {
                    return args.onload(xhr);
                } else {
                    return args.onerror(xhr);
                }
            }
        };

        if(_DEBUG) {
            ax.debug('ajax: {0}', JSON.stringify(args));
            debug_time = new Date().getTime();
        }

        try {
            xhr.open(args.method||'GET', args.url, !!args.async);
            foreach(args.headers, function(k, v) {
                xhr.setRequestHeader(k, v);
            });
            xhr.send(args.data);
        } catch (e) {
            args.onerror(xhr, e);
        }

        return xhr;
    }

    /**
     * format string with variable arguments
     *
     * <pre>
     * '1 token, 0 args ({0})' -- '1 token, 0 args ({0})'
     * '1 token, 1 args ({0})', 'arg1' -- '1 token, 1 args (arg1)'
     * '1 tokens, 2 args ({0})', 'arg1', 'arg2' -- '1 tokens, 2 args (arg1)'
     * '2 tokens, 2 args ({0},{1})', 'arg1', 'arg2' -- '2 tokens, 2 args(arg1,arg2)'
     * '2 tokens swapped, 2 args ({1},{0})', 'arg1', 'arg2' -- '2 tokens swapped, 2 args (arg2,arg1)'
     * '4 tokens interwoven, 2 args ({0},{1},{0},{1})', 'arg1', 'arg2' -- '4 tokens interwoven, 2 args (arg1,arg2,arg1,arg2)'
     * </pre>
     *
     * @param {string} text
     * @param {...*} var_args
     * @return {string}
     * @methodOf ax.util
     */
    function format(text) {
        if (arguments.length < 2) {
            return String(text);
        }

        // get sub-array from 1 to end
        var args = Array.prototype.slice.call(arguments, [1]);

        ax.util.foreach(args, function(k, v) {
            text = text.replace(new RegExp('\\{' + k + '\\}', 'gi'), v);
        });

        return text;
    }

    var INVALID_FUNCTION = '<<invalid-function>>';
    var ANONYMOUS_FUNCTION = '<<anonymous-function>>';
    var FUNCTION_REGEXP = /function\s+(\w+)/;

    /**
     * get/guess function name from javascript function object.
     *
     * @param {function} func
     * @return {string} function name if available(named or named-inline), otherwise <<anonymous>> or <<invalid-function>>
     * @memberOf ax.util
     */
    function getFunctionName(func) {
        if (!ax.isFunction(func)) {
            return INVALID_FUNCTION;
        }
        var match = FUNCTION_REGEXP.exec(func);
        return match ? match[1] : ANONYMOUS_FUNCTION;
    }

    /**
     *
     * @param {object} obj
     * @param {function} func
     * @param args...
     * @memberOf ax.util
     */
    function invokeLater(obj, func) {
        var args = Array.prototype.slice.call(arguments, 2);
        window.setTimeout(function() {
            try {
                func.apply(obj, args);
            } catch (e) {
                ax.log('uncaught exception from function {1}: {0} ', e,
                        getFunctionName(func));
            }
        }, 1);
    }

    /**
     * validate parameter.
     *
     * @param {*}
     *            param
     * @param {boolean}
     *            mandatory
     * @param {boolean}
     *            nullable
     * @param {string}
     *            type "string" or "number"
     * @param {boolean}
     *            exception
     * @param {string}
     *            name
     * @return {boolean}
     * @memberOf ax.util
     */
    function validateParam(param, mandatory, nullable, type, exception, name) {
        var msg;

        if (undefined === param) {
            if (mandatory) {
                msg = format('{0}: mandatory parameter is omitted', name);
                ax.log(msg);

                if (exception) {
                    throw new ax.error(ax.INVALID_VALUES_ERR, msg);
                }

                return false;
            }
        } else if (null === param) {
            if (!nullable) {
                msg = format('{0}: parameter can not be null', name);
                ax.log(msg);

                if (exception) {
                    throw new ax.error(ax.INVALID_VALUES_ERR, msg);
                }

                return false;
            }
        } else if (typeof param !== type) {
            if (type === 'string') {
                return true;
            } else if (type === 'number') {
                if (!isNaN(parseFloat(param)) && isFinite(param)) {
                    return true;
                }
            }

            msg = format('{0}: must be a {1} type', name, type);
            ax.log(msg);

            if (exception) {
                throw new ax.error(ax.TYPE_MISMATCH_ERR, msg);
            }
            return false;
        }

        return true;
    }

    /**
     * validate instance.
     *
     * return true when the "value" object is instance of "constructor"
     * function(aka class).
     *
     * @param {object}
     *            value
     * @param {function}
     *            constructor
     * @param {boolean}
     *            exception
     * @param {string}
     *            name
     * @return {boolean}
     * @memberOf ax.util
     */
    function validateInstance(value, constructor, exception, name) {
        if (value && !(value instanceof constructor)) {
            if (exception) {
                throw new ax.error(ax.TYPE_MISMATCH_ERR, name + ': is not a '
                        + getFunctionName(constructor));
            }
            return false;
        }
        return true;
    }

    // ----------------------------------------------------

    /*
     *very very simple pubsub.
     */
    var topics = {};

    /**
     * subscribe (add callback for topic)
     *
     * @param {string} topic
     * @param {function} callback
     */
    function subscribe(topic, callback) {
        if (!topics[topic]) {
            topics[topic] = [];
        }
        topics[topic].push(callback);
    }

    /**
     * unsubscribe (remove callback for topic)
     *
     * @param {string} topic
     * @param {function} callback
     */
    function unsubscribe(topic, callback) {
        foreach(topics[topic], function(k, v) {
            if (v === callback) {
                topics[topic].splice(k, 1);
                return true;
            }
        });
    }

    /**
     * notify to all subscribers (invoke all callbacks for topic)
     *
     * @param {string} topic
     * @param {array} args
     */
    function publish(topic, args) {
        foreach(topics[topic], function(k, v) {
            invokeLater(null, v, args);
        });
    }


    // "..", "//", "/./", ^"./", ^""$, ^"."$ 등 경로에 들어가면 안되는 패턴들...
    var INVALID_PATH_REGEX = /\/\/|\.\.|\.\/|^$|^\.$/;

    /**
     * validate inputed path is available dir/file
     *
     * @param {...string} var_args
     * @return {boolean}
     * @memberOf ax.util
     */
    function isValidPath(){
        for( var i = 0; i < arguments.length; i++ ){
            if(INVALID_PATH_REGEX.test(arguments[i])) {
                return false;
            }
        }
        return true;
    }

    // ----------------------------------------------------

    /**
     * very simple config
     */
    var configs = {};

    function getConfig(key) {
        return configs[key];
    }

    function setConfig(key, value) {
        configs[key] = value;
    }

    // ----------------------------------------------------

    ax.def(util)
        .method('evaluateJavaScript', evaluateJavaScript)
        .method('encodeJSON', encodeJSON)
        .method('decodeJSON', decodeJSON)
        .method('parseXML', parseXML)
        .method('foreach', foreach)
        .method('dump', dump)
        .method('ajax', ajax)
        .method('format', format)
        .method('getFunctionName', getFunctionName)
        .method('invokeLater', invokeLater)
        .method('validateParam', validateParam)
        .method('validateInstance', validateInstance)
        .method('subscribe', subscribe)
        .method('unsubscribe', unsubscribe)
        .method('publish', publish)
        .method('isValidPath',isValidPath)
        .method('getConfig', getConfig)
        .method('setConfig', setConfig);

    ax.def(ax).constant('util', util);
    // ====================================================
}(window));
