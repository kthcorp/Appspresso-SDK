/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    /**
     * root namespace for appspresso javascript api.
     *
     * @name ax
     * @namespace root namespace for appspresso javascript api
     */
    var ax = {};

    /**
     * runMode for DEBUG. verbose log messages and error checks.
     *
     * @name MODE_DEBUG
     * @constant
     * @fieldOf ax
     */
    var MODE_DEBUG = 0;

    /**
     * runMode for RELEASE. less logs messages and error checks.
     *
     * @name MODE_RELEASE
     * @constant
     * @fieldOf ax
     */
    var MODE_RELEASE = 1;

    /**
     * ax.MODE_DEBUG or ax.MODE_RELEASE.
     *
     * @name runMode
     * @fieldOf ax
     */
    var runMode = MODE_RELEASE;

    /*
     * getter for runMode
     */
    function getRunMode() {
        return runMode;
    }

    /*
     * setter for runMode
     */
    function setRunMode(newRunMode) {
        //debug('switch runMode to ' + newRunMode);
        runMode = newRunMode;
    }


    // ----------------------------------------------------

    /**
     * no operation. used for dummy callback.
     *
     * @methodOf ax
     */
    function nop() {
        // console.trace('nop');
    }

    /**
     * assert condition.
     *
     * @param {boolean} condition
     * @param {string} message
     * @methodOf ax
     */
    function assert(condition, message) {
        if (!condition) {
            throw new Error('assertion failed:' + message);
        }
    }

    // ----------------------------------------------------

    /**
     * log for plugin developer.
     *
     * <pre>
     * ax.log('1 token, 0 args ({0})'); // 1 token, 0 args ({0})
     * ax.log('1 token, 1 args ({0})', 'arg1'); // 1 token, 1 args (arg1)
     * ax.log('1 tokens, 2 args ({0})', 'arg1', 'arg2'); // 1 tokens, 2 args (arg1)
     * ax.log('2 tokens, 2 args ({0},{1})', 'arg1', 'arg2'); // 2 tokens, 2 args (arg1,arg2)
     * ax.log('2 tokens swapped, 2 args ({1},{0})', 'arg1', 'arg2'); // 2 tokens swapped, 2 args (arg2,arg1)
     * ax.log('4 tokens interwoven, 2 args ({0},{1},{0},{1})', 'arg1', 'arg2'); // 4 tokens interwoven, 2 args (arg1,arg2,arg1,arg2)
     * </pre>
     *
     * @param {string} text
     * @param {...*} var_args
     * @methodOf ax
     */
    function log() {
        if (MODE_DEBUG === runMode) {
            var str = ax.util.format.apply(null, arguments);
            console.log(str);
        }
    }

    /**
     * log for internal developer.
     *
     * @param {string} text
     * @param {...*} var_args
     * @methodOf ax
     */
    function debug() {
        if (_DEBUG) {
            var str = ax.util.format.apply(null, arguments);
            console.log('[internal] ' + str);
        }
    }

    // ----------------------------------------------------

    /**
     * check whether "arg" is "object".
     *
     * @param {*} arg
     * @return {boolean}
     * @methodOf ax
     */
    function isObject(arg) {
        return (arg !== undefined) && (typeof arg === 'object');
    }

    /**
     * check whether "arg" is "function".
     *
     * @param {*} arg
     * @return {boolean}
     * @methodOf ax
     */
    function isFunction(arg) {
        // this code is borrowed from dojo
        return Object.prototype.toString.call(arg) === '[object Function]';
        //some array-like object is a 'function'
        //return (arg !== undefined) && (typeof arg === 'function');
    }

    /**
     * check whether "arg" is "string".
     *
     * @param {*} arg
     * @return {boolean}
     * @methodOf ax
     */
    function isString(arg) {
        return (typeof arg === 'string') || (arg instanceof String);
    }

    /**
     * check whether "arg" is "number".
     *
     * @param {*} arg
     * @return {boolean}
     * @methodOf ax
     */
    function isNumber(arg) {
        return (arg !== undefined) && (typeof arg === 'number') && (!isNaN(arg));
    }

    /**
     * check whether "arg" is "boolean".
     *
     * @param {*} arg
     * @return {boolean}
     * @methodOf ax
     */
    function isBoolean(arg) {
        return (arg !== undefined) && (typeof arg === 'boolean');
    }

    /**
     * check whether "arg" is "array".
     *
     * @param {*} arg
     * @return {boolean}
     * @methodOf ax
     */
    function isArray(arg) {
        return (arg !== undefined) && (arg instanceof Array);
        // || (typeof arg === 'array')
    }

    /**
     * check whether "arg" is "array-like"(NodeList, arguments, ... and so on).
     *
     * @param {*} arg
     * @return {boolean}
     * @methodOf ax
     */
    function isArrayLike(arg) {
        return (arg !== undefined) && !isString(arg) && !isFunction(arg)
            && ((arg instanceof Array) || isFinite(arg.length));
    }

    // ----------------------------------------------------

    /**
     * This class provides helper methods to define/decorate javascript object.
     *
     * use ax.def() to create an instance of this class.
     *
     * @param {object} target (optional)
     * @name AxDef
     * @class
     * @contructor
     * @see ax.def
     */
    function AxDef(target) {
        this.target = target||{};
    }

    /**
     * define a constant property.
     *
     * @param {string} name
     * @param {*} value
     * @return {AxDef}
     * @methodOf AxDef.prototype
     */
    AxDef.prototype.constant = function(name, value) {
        //console.log('defconst: target=', target, 'name=', name, 'value=', value);
        if (!isString(name)) {
            throw new Error('invalid parameter: name');
        }
        //target.__defineSetter__(name, nop);
        this.target.__defineGetter__(name, function() {
            return value;
        });
        return this;
    };

    /**
     * define a read/write property.
     *
     * @param {string} name
     * @param {function} getter null for no getter
     * @param {function} setter null for no setter
     * @return {AxDef}
     * @methodOf AxDef.prototype
     */
    AxDef.prototype.property = function(name, getter, setter) {
        if (!isString(name)) {
            throw new Error('invalid parameter: name');
        }
        if (getter) {
            if (!isFunction(getter)) {
                throw new Error('invalid parameter: getter');
            }
            this.target.__defineGetter__(name, getter);
        }
        if (setter) {
            if (!isFunction(setter)) {
                throw new Error('invalid parameter: setter');
            }
            this.target.__defineSetter__(name, setter);
        }
        return this;
    };

    /**
     * define a method.
     *
     * @param {string} name
     * @param {function} func
     * @return {AxDef}
     * @methodOf AxDef.prototype
     */
    AxDef.prototype.method = function(name, func) {
        if (!isString(name)) {
            throw new Error('invalid parameter: name');
        }
        if (!isFunction(func)) {
            throw new Error('invalid parameter: func');
        }
        // XXX: to hide function body
        // func.toString = function() {
        // return '<<method:' + name + '>>';
        // };
        this.target.__defineGetter__(name, function() {
            return func;
        });
        return this;
    };

    /**
     * define a namespace.
     *
     * @param {string} name (period(.)-separated names)
     * @param {object} parent (optional. default: "window")
     * @return {AxDef}
     * @methodOf AxDef.prototype
     */
    AxDef.prototype.ns = function(name, parent) {
        var i, term, terms = name.split('.');
        if (!parent) {
            parent = g;
        }

        for (i = 0; i < terms.length - 1; i += 1) {
            term = terms[i];
            if (!parent.hasOwnProperty(term)) {
                // empty for interim namespace
                parent[term] = {};
            }
            parent = parent[term];
        }

        term = terms[i];
        parent[term] = this.target;

        return this;
    };

    /**
     * merge all properties(and methods) from another object.
     *
     * @param {object} obj
     * @return {AxDef}
     * @methodOf AxDef.prototype
     */
    AxDef.prototype.mixin = function(obj) {
        if (obj) {
            var k, v;
            for (k in obj) {
                if (obj.hasOwnProperty(k)) {
                    v = obj[k];
                    if (!this.target.hasOwnProperty(k) || (this.target[k] !== v)) {
                        this.target[k] = v;
                    }
                }
            }
        }
        return this;
    };

    /**
     * end of def.
     *
     * @return {object} a defined/decorated target object
     * @methodOf AxDef.prototype
     */
    AxDef.prototype.end = function() {
        return this.target;
    };

    /**
     * define/decorate an object with additional constants, properties and methods with a chain of method calls.
     *
     * to define a new object:
     * <pre>
     *     var obj = ax.def().constant(...).property(...).method(...).end();
     * </pre>
     *
     * to decorate an existing object:
     * <pre>
     *     ax.def(obj).constant(...).property(...).method(...).end();
     * </pre>
     *
     *
     * @param {object} target (optional)
     * @return {AxDef}
     * @methodOf ax
     */
    function def(target) {
        return new AxDef(target);
    }
    // ----------------------------------------------------

    def(ax)
        .constant('MODE_DEBUG', MODE_DEBUG)
        .constant('MODE_RELEASE', MODE_RELEASE)
        .property('runMode', getRunMode, setRunMode)
        .method('nop', nop)
        .method('assert', assert)
        .method('log', log)
        .method('debug', debug)
        .method('isObject', isObject)
        .method('isFunction', isFunction)
        .method('isString', isString)
        .method('isNumber', isNumber)
        .method('isBoolean', isBoolean)
        .method('isArray', isArray)
        .method('isArrayLike', isArrayLike)
        .method('def', def);

    def(g).constant('ax', ax);
    // ====================================================
}(window));
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
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.event
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    var event = {};

    var handlersMap = {};

    var onetimeMap = {};

    function on(type, handler) {
        var handlers = handlersMap[type] || (handlersMap[type] = []);
        handlers.push(handler);
        return this;
    }

    function off(type, handler) {
        if (!handler) {
            delete handlersMap[type];
            delete onetimeMap[type];
            return this;
        }

        var handlers = handlersMap[type],
            onetimers = onetimeMap[type];

        if (handlers) {
            handlersMap[type] = handlers.filter(function(fn) { return fn !== handler; });
        }

        if (onetimers) {
            onetimeMap[type] = onetimers.filter(function(fn) { return fn !== handler; });
        }

        return this;
    }

    function one(type, handler) {
        var handlers = onetimeMap[type] || (onetimeMap[type] = []);
        handlers.push(handler);
        return this;
    }

    function call(fn, params) {
        try {
            fn.apply(null, params);
        } catch (e) {
            ax.log('uncaught exception from function {0}: {1}',
                ax.util.getFunctionName(fn),
                ax.util.encodeJSON(e));
        }
    }

    function trigger(type, params) {
        var handlers = handlersMap[type] || [];
        handlers.forEach(function(handler) {
            call(handler, params);
        });

        handlers = onetimeMap[type] || [];
        handlers.forEach(function(handler) {
            call(handler, params);
        });

        delete onetimeMap[type];
        return this;
    }

    var states = {};

    var queue = {};

    function ready(type, handler) {
        var state = states[type];
        if (!state) {
            (queue[type] || (queue[type] = [])).push(handler);
            return this;
        }

        ax.util.invokeLater(null, function() {
            call(handler, state.params);
        });
        return this;
    }

    function done(type, params) {
        var handlers = queue[type] || [];
        delete queue[type];

        handlers.forEach(function(handler) {
            call(handler, params);
        });

        states[type] = { params: params };
        return this;
    }

    ax.def(event)
        .method('on', on)
        .method('off', off)
        .method('one', one)
        .method('trigger', trigger)
        .method('ready', ready)
        .method('done', done);

    ax.def(ax).constant('event', event);
    // ====================================================
}(window));
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
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.cbstore
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    /**
     * 비동기 호출이나 watch 호출의 callback 을 관리
     *
     * @name ax.cbstore
     * @namespace
     */
    var cbstore = {};

    var store = {};

    function register(id, cb, eb) {
        store[id] = {
            cb: cb,
            eb: eb
        };
        return this;
    }

    function clear(id) {
        delete store[id];
        return this;
    }

    function peek(id) {
        return store[id];
    }

    function pop(id) {
        var cbs = store[id];
        delete store[id];
        return cbs;
    }

    ax.def(cbstore)
        .method('register', register)
        .method('clear', clear)
        .method('peek', peek)
        .method('pop', pop);

    ax.def(ax).constant('cbstore', cbstore);

})(window);
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
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.rpcpoll
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    var bridge = ax.bridge;

    var jsonrpc = ax.jsonrpc;

    var xhrArgs = {
        method: 'GET',
        async: true,
        onload: onLoad,
        onerror: onError
    };

    var APIURL = '/appspresso/rpcpoll/';

    // bridge session id.
    var session = bridge.session();

    // poll sequence no.
    var seq = 0;

    var proxy = (function() {
        //states
        var STOP = 0,
            NORMAL = 1,
            TIMERMODE = 2;

        var WATCH_INTERVAL = 5000,
            POLL_TIMEOUT = 10000,
            SANITY_MARGIN = 1.2,
            ERROR_THRESHOLD = 10;

        var state = STOP;

        var active, activet, errcnt = 0;

        function request() {
            active = poll();
            activet = Date.now();
        }

        function repoll(h) {
            if (h !== active) {
                return;
            }
            request();
        }

        function toolong() {
            return POLL_TIMEOUT * SANITY_MARGIN;
        }

        // public methods =================================
        function start() {
            if (state === STOP) {
                state = NORMAL;
                request();
            }
        }

        function stop() {
            state = STOP;
        }

        function once() {
            if (state === STOP) {
                request();
            }
        }

        function success(h) {
            errcnt = 0;

            switch (state) {
            case STOP:
                // do nothing
                break;

            case NORMAL:
                repoll(h);
                break;

            case TIMERMODE:
                state = NORMAL;
                repoll(h);
                break;
            }
        }

        function empty(h) {
            if (h === active) {
                POLL_TIMEOUT = (POLL_TIMEOUT + (Date.now() - activet)) / 2;
            }
            return success(h);
        }

        function error(h) {
            switch (state) {
            case STOP:
                errcnt = 0;
                break;

            case NORMAL:
                if (++errcnt > ERROR_THRESHOLD) {
                    state = TIMERMODE;
                    break;
                }
                repoll(h);
                break;

            case TIMERMODE:
                // do nothing
                break;
            }
        }

        // watch dog =====================================
        function watcher() {
            switch (state) {
            case STOP:
                // do nothing
                break;

            case NORMAL:
                if (Date.now() - activet > toolong()) {
                    request();
                }
                break;

            case TIMERMODE:
                request();
                break;
            }
        }

        g.setInterval(watcher, WATCH_INTERVAL);

        return {
            start: start,
            stop: stop,
            once: once,
            success: success,
            empty: empty,
            error: error
        }

    })(rpcpoll);

    function emptyResponse(resp) {
        return !resp || resp.length === 0;
    }

    function onLoad(xhr) {
        if (emptyResponse(xhr.responseText)) {
            if (_DEBUG) {
                ax.debug('rpcpoll: empty response. retry.');
            }
            return proxy.empty(xhr);
        }

        proxy.success(xhr);

        if (_DEBUG) {
            ax.debug('rpcpoll: {0}', xhr.responseText);
        }

        bridge.jsonrpc(xhr.responseText);
    }

    function onError(xhr) {
        if (_DEBUG) {
            ax.debug('rpcpoll error: {0} {1}', xhr.status, xhr.statusText);
        }
        proxy.error(xhr);
    }

    function poll() {
        xhrArgs.url = (g._APPSPRESSO_RPCPOLL_URL || APIURL) + '?session=' + session + '&seq=' + seq++;
        return ax.util.ajax(xhrArgs);
    }

    /**
     * rpcpoll start switch
     *
     * @methodOf ax.bridge.rpcpoll
     */
    function start() {
        proxy.start();
    }

    /**
     * rpcpoll stop switch
     *
     * @methodOf ax.bridge.rpcpoll
     */
    function stop() {
        proxy.stop();
    }

    /**
     * just one poll (for debugging purpose)
     *
     * @methodOf ax.bridge.rpcpoll
     */
    function _once() {
        proxy.once();
    }

    var rpcpoll = {};

    ax.def(rpcpoll)
        .method('start', start)
        .method('stop', stop)
        .method('_once', _once);

    ax.def(bridge)
        .constant('rpcpoll', rpcpoll);

    ax.event.on('startrpcpoll', function() {
        rpcpoll.start();
    });

})(window);
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.plugin
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    /**
     * XXX: all registered plugins
     */
    var plugins = {};

    /**
     * javascript stub for appspresso plugin.
     *
     * use ax.plugin() to create an instance of this class.
     *
     * use ax.isPlugin() to check an object is an instance of this class
     *
     * @param {string} prefix
     * @name AxPlugin
     * @class
     * @constructor
     * @see ax.plugin
     * @see ax.isPlugin
     */
    function AxPlugin(prefix) {
        this.prefix = prefix;
    }

    /**
     * call native plugin: sync.
     *
     * @param {string} method
     * @param {array} params
     * @return {*}
     * @methodOf AxPlugin.prototype
     */
    function execSync(method, params) {
        var fullmethod = [this.prefix, method].join('.');
        return ax.bridge.execSync(fullmethod, params||[]);
    }

    /**
     * call native plugin: async
     *
     * @param {string} method
     * @param {function} successCallback
     * @param {function} errorCallback
     * @param {array} params
     * @param {number} [_id] debugging purpose
     * @return {AxRequest}
     * @methodOf AxPlugin.prototype
     */
    function execAsync(method, successCallback, errorCallback, params, _id) {
        var fullmethod = [this.prefix, method].join('.');
        return ax.bridge.execAsync(fullmethod, successCallback, errorCallback, params||[], _id);
    }

    /**
     * reg watch func.
     *
     * @param {string} method
     * @param {function} successCallback
     * @param {function} errorCallback
     * @param {array} params
     * @return {number} watchID
     * @methodOf AxPlugin.prototype
     */
    function watch(method, successCallback, errorCallback, params) {
        var fullmethod = [this.prefix, method].join('.');
        var watchID = ax.bridge.watch(fullmethod, successCallback, errorCallback, params||[]);
        return watchID;
    }

    /**
     * stop watch and unreg watch func
     *
     * @param {string} method
     * @param {number} watchID
     * @methodOf AxPlugin.prototype
     */
    function stopWatch(method, watchID) {
        var fullmethod = [this.prefix, method].join('.');
        ax.bridge.stopWatch(fullmethod, watchID);
    }

    // ----------------------------------------------------

    ax.def(AxPlugin.prototype)
        .method('execSync', execSync)
        .method('execAsync', execAsync)
        .method('watch', watch)
        .method('stopWatch', stopWatch);

    // ----------------------------------------------------

    /**
     * factory method to create new "AxPlugin" object.
     *
     * @param {string} name
     * @param {object} obj
     * @param {string} ns (optional)
     * @return {AxPlugin}
     * @methodOf ax
     */
    function plugin(name, obj, ns) {
        if (!ax.isString(name)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid paramter: name');
        }

        if (!ax.isObject(obj)) {
            throw ax.error(ax.TYPE_MISMATCH_ERR, 'invalid parameter: obj');
        }

        //if (plugins[name]) {
        //    throw ax.error(ax.TYPE_MISMATCH_ERR, 'duplicated plugin name');
        //}

        if(_DEBUG) {
            ax.debug('create a plugin: ', name);
        }

        // extends AxPlugin(inject execSync/execAsync methods and so on...)
        var pluginObj = ax.def(new AxPlugin(name)).mixin(obj).end();

        // declares namespace(global variable)
        if(ax.isString(ns)) {
            ax.def(pluginObj).ns(ns);
        }

        // register for later use
        plugins[name] = pluginObj;

        return pluginObj;
    }

    /**
     * check where "obj" is "AxPlugin" object.
     *
     * @param {object} obj
     * @return {boolean}
     * @methodOf ax
     */
    function isPlugin(obj) {
        return obj && (obj instanceof AxPlugin);
    }

    // ----------------------------------------------------

    ax.def(ax)
        .constant('AxPlugin', AxPlugin) // XXX: deviceapis extends AxPlugin...
        .method('plugin', plugin)
        .method('isPlugin', isPlugin);
    // ====================================================
}(window));
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview ax.console
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    /* keep browser built-in console */
    var consoleOrig = g.console;

    /**
     * appspresso console.
     *
     * @name ax.console
     * @namespace
     * @see http://wiki.commonjs.org/wiki/Console
     * @see http://getfirebug.com/wiki/index.php/Console_API
     */
    var console = {};

    /**
     * @name LEVEL_TRACE
     * @constant
     * @fieldOf ax.console
     */
    var LEVEL_TRACE = 0;

    /**
     * @name LEVEL_DEBUG
     * @constant
     * @fieldOf ax.console
     */
    var LEVEL_DEBUG = 1;

    /**
     * @name LEVEL_INFO
     * @constant
     * @fieldOf ax.console
     */
    var LEVEL_INFO = 2;

    /**
     * @name LEVEL_WARN
     * @constant
     * @fieldOf ax.console
     */
    var LEVEL_WARN = 3;

    /**
     * @name LEVEL_ERROR
     * @constant
     * @fieldOf ax.console
     */
    var LEVEL_ERROR = 4;

    /**
     * @name LEVEL_ALL
     * @constant
     * @fieldOf ax.console
     */
    var LEVEL_ALL = 0;

    /**
     * @name LEVEL_NONE
     * @constant
     * @fieldOf ax.console
     */
    var LEVEL_NONE = 999;

    /**
     * granted log level.
     *
     * @fieldOf ax.console
     */
    var level = LEVEL_ALL;

    /**
     *
     * @name level
     * @fieldOf ax.console
     */
    function getLevel() {
        return level;
    }

    /**
     *
     * @name level
     * @fieldOf ax.console
     */
    function setLevel(newLevel) {
        level = newLevel;
    }

    /**
     *
     * @name traceEnabled
     * @fieldOf ax.console
     */
    function isTraceEnabled() {
        return level <= LEVEL_TRACE;
    }

    /**
     *
     * @name debugEnabled
     * @fieldOf ax.console
     */
    function isDebugEnabled() {
        return level <= LEVEL_DEBUG;
    }

    /**
     *
     * @name infoEnabled
     * @fieldOf ax.console
     */
    function isInfoEnabled() {
        return level <= LEVEL_INFO;
    }

    /**
     *
     * @name warnEnabled
     * @fieldOf ax.console
     */
    function isWarnEnabled() {
        return level <= LEVEL_WARN;
    }

    /**
     *
     * @name errorEnabled
     * @fieldOf ax.console
     */
    function isErrorEnabled() {
        return level <= LEVEL_ERROR;
    }

    /*
     * not yet sent logs(aka buffer)
     *
     * @private
     */
    var logBuffer = [];

    /**
     *
     * @private
     */
    var LOG_BUFFER_SIZE = 10;

    /*
     * time interval for flushLogs()
     *
     * @private
     */
    var LOG_INTERVAL = 500;

    /*
     * interval object for flushLogs()
     *
     * @private
     */
    var logInterval = null;

    /*
     * url prefix for log receiver on native side.
     *
     * NOTE: could be overridden via undocumented global variable "_APPSPRESSO_LOG_URL"
     *
     * @private
     */
    var LOG_URL = '/appspresso/LOG$/';
    //var LOG_URL = /'ax:console:';

    /*
     * flush all logs in buffer.
     *
     * @private
     */
    function flushLogs() {
        if (logBuffer.length <= 0) { return; }

        // send all logs in buffer "at once" via ajax post
        var data = [];
        ax.util.foreach(logBuffer, function(k, v) {
            // level/sequence/file/line/message
            // TODO: get(guess) file and line
            data.push([
                v.level || LEVEL_INFO,
                v.sequence || -1,
                g.encodeURIComponent(v.file || g.location.pathname),
                v.line || 0,
                g.encodeURIComponent(v.message || '')
            ].join('/'));
        });
        logBuffer.length = 0;
        data = data.join('\n');
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            // fallback: emit logs to original console
            if(xhr.readyState === 0 && xhr.status === 404) {
                consoleOrig.log('[LOG]' + data);
            }
        };
        xhr.open('POST', (g._APPSPRESSO_LOG_URL||LOG_URL));
        xhr.setRequestHeader('Content-Type', 'text/plain;charset=UTF-8');
        xhr.send(data);
    }

    /*
     * emit log(buffer or send).
     *
     * @param {object} log
     * @private
     */
    function sendLog(log) {
        logBuffer.push(log);
        if(logBuffer.length > LOG_BUFFER_SIZE) {
            flushLogs();
        }
    }

    /*
     * format log message
     *
     * @param {*) args
     * @private
     */
    function formatLogMessage(args) {
        // shallow dump w/o intent/newline/separator
        return ax.util.dump(args, 1, '', '', ' ');
    }

    /*
     *
     * @private
     */
    function watchLog() {
        logInterval = setInterval(flushLogs, LOG_INTERVAL);
    }

    /*
     *
     * @private
     */
    function stopWatchLog() {
        clearInterval(logInterval);
        logInterval = null;
        flushLogs();
    }

    /*
     * url prefix for console receiver on native side.
     *
     * NOTE: could be overridden via undocumented global variable "_APPSPRESSO_CONSOLE_URL"
     *
     * @private
     */
    var CONSOLE_URL = '/appspresso/CON$/';

    /*
     * time interval for fetchCommands()
     *
     * @private
     */
    var CONSOLE_INTERVAL = 500;

    /*
     * interval object for fetchCommands()
     *
     * @private
     */
    var consoleInterval = null;

    /*
     * evaluate the javascript expression and send result via log.
     *
     * @param {string} expression
     * @private
     */
    function doEvaluate(expression, sequence) {
        if (typeof expression === 'string') {
            expression = expression.replace(/;+$/, '');
        }
        var result;
        try {
            result = ax.util.evaluateJavaScript(expression);
        } catch(e) {
            result = e;
        }
        sendLog({
            level: LEVEL_INFO,
            sequence: sequence,
            message: ax.util.dump(result)
        });
    }

    var debugSessionKey = '';

    /*
     * fetch command via ajax.
     *
     * TODO: reimplement with async push such as websocket, comet, ...
     *
     * @private
     */
    function fetchCommands() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if(xhr.readyState === 4 && xhr.status === 200) {
                if(!xhr.responseText) { return; }
                ax.util.foreach(xhr.responseText.split('\n'), function(k, v) {
                    var expr = v.split(' '),
                        command = expr.shift();

                    if (command === 'evaluate') {
                        ax.util.invokeLater(null, doEvaluate, g.decodeURIComponent(expr.join(' ')));
                    }
                    else if (command === 'evaluateWithSequence') {
                        var seq = parseInt(expr.shift());
                        ax.util.invokeLater(null, doEvaluate, g.decodeURIComponent(expr.join(' ')), seq);
                    }
                    else if (command === 'reissueSession') {
                        if (sessionState === 'issuing') {
                            return;
                        }

                        sessionState = 'revoked';
                        issueDebugSession(ax.util.getConfig('device'));
                    }
                });
            }
        };
        xhr.open('POST', (g._APPSPRESSO_CONSOLE_URL||CONSOLE_URL));
        xhr.setRequestHeader('Content-Type', 'text/plain; charset=UTF-8');
        xhr.send('Session-Key=' + debugSessionKey);
    }

    /*
     *
     * @private
     */
    function watchConsole() {
        consoleInterval = g.setInterval(fetchCommands, CONSOLE_INTERVAL);
    }

    /*
     *
     * @private
     */
    function stopWatchConsole() {
        g.clearInterval(consoleInterval);
        consoleInterval = null;
    }

    /**
     * logs a static/interactive listings of all properties of the object
     *
     * @param {*} obj
     * @methodOf ax.console
     */
    function dir(obj) {
        sendLog({
            level: LEVEL_INFO,
            message: ax.util.dump(obj)
        });
    }

    /**
     * logs a message.
     *
     * @param {...*} var_args
     */
    function log() {
        sendLog({
            level: LEVEL_INFO,
            message: formatLogMessage(arguments)
        });
    }

    /**
     * logs a message.
     *
     * @param {...*} var_args
     * @methodOf ax.console
     */
    function trace() {
        if (level > LEVEL_TRACE) { return; }
        sendLog({
            level: LEVEL_TRACE,
            message: formatLogMessage(arguments)
        });
    }

    /**
     * logs a message with visual "debug" representation.
     *
     * @param {...*} var_args
     * @methodOf ax.console
     */
    function debug() {
        if (level > LEVEL_DEBUG) { return; }
        sendLog({
            level: LEVEL_DEBUG,
            message: formatLogMessage(arguments)
        });
    }

    /**
     * logs a message with visual "information" representation.
     *
     * @param {...*} var_args
     * @methodOf ax.console
     */
    function info() {
        if (level > LEVEL_INFO) { return; }
        sendLog({
            level: LEVEL_INFO,
            message: formatLogMessage(arguments)
        });
    }

    /**
     * logs a message with visual "warning" representation.
     *
     * @param {...*} var_args
     * @methodOf ax.console
     */
    function warn() {
        if (level > LEVEL_WARN) { return; }
        sendLog({
            level: LEVEL_WARN,
            message: formatLogMessage(arguments)
        });
    }

    /**
     * logs a message with visual "error" representation.
     *
     * @param {...*} var_args
     * @methodOf ax.console
     */
    function error() {
        if (level > LEVEL_ERROR) { return; }
        sendLog({
            level: LEVEL_ERROR,
            message: formatLogMessage(arguments)
        });
    }

    /**
     * start redirecting logs to browser built-in console.
     *
     * @methodOf ax.console
     */
    function startRedirect() {
        // XXX: override buit-in console
        g.console = {
            dir : dir,
            log : log,
            trace : trace,
            debug : debug,
            info : info,
            warn : warn,
            error : error
        };
        watchLog();
        watchConsole();
    }

    /**
     * stop redirecting logs to browser built-in console.
     *
     * @methodOf ax.console
     */
    function stopRedirect() {
        // XXX: restore buit-in console
        g.console = consoleOrig;
        stopWatchLog();
        stopWatchConsole();
    }

    var statusPlugin =
        ax.def(ax.plugin('ax.builtin.devicestatus', {}))
            .method('vendor', function() {
                return this.execSync('getVendor');
            })
            .method('model', function() {
                return this.execSync('getModel');
            })
        .end();

    var sessionState = 'absence';

    function issueDebugSession(info) {
        sessionState = 'issuing';

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    debugSessionKey = xhr.responseText;
                    sessionState = 'valid';
                } else {
                    sessionState = 'absence';
                }
            }
        };
        xhr.open('POST', g._APPSPRESSO_DEBUG_SESSION_ISSUE_URL);
        xhr.setRequestHeader('Content-Type', 'text/plain; charset=UTF-8');
        xhr.send(JSON.stringify(info));
    }

    /**
     * set device specific debug info (e.g. vendor, model) then receive session key from nessie
     * called from runtime (on-the-fly trailing script)
     *
     * @methodOf ax.console
     */
    function initDebugSession() {
        var info = {
            name: [statusPlugin.model(), statusPlugin.vendor()].join('@'),
            width: g.screen.availWidth,
            height: g.screen.availHeight,
            port: location.port
        };

        ax.util.setConfig('device', info);
        issueDebugSession(info);
    }

    ax.def(console)
        .constant('LEVEL_NONE', LEVEL_NONE)
        .constant('LEVEL_TRACE', LEVEL_TRACE)
        .constant('LEVEL_DEBUG', LEVEL_DEBUG)
        .constant('LEVEL_INFO', LEVEL_INFO)
        .constant('LEVEL_WARN', LEVEL_WARN)
        .constant('LEVEL_ERROR', LEVEL_ERROR)
        .constant('LEVEL_ALL', LEVEL_ALL)
        .property('level', getLevel, setLevel)
        .property('traceEnabled', isTraceEnabled)
        .property('debugEnabled', isDebugEnabled)
        .property('infoEnabled', isInfoEnabled)
        .property('warnEnabled', isWarnEnabled)
        .property('errorEnabled', isErrorEnabled)
        .method('trace', trace)
        .method('debug', debug)
        .method('info', info)
        .method('warn', warn)
        .method('error', error)
        .method('startRedirect', startRedirect)
        .method('stopRedirect', stopRedirect)
        .method('initDebugSession', initDebugSession);

    ax.def(ax).constant('console', console);
    // ====================================================
}(window));
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview This file provider W3C Widgets API
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    var CONFIG_XML_URL = '/config.xml';

    /**
     *
     * @name widget
     * @namespace
     */
    var widget = null;

    /**
     * This class represents W3C Widgets API.
     *
     * @name Widget
     * @class
     * @constructor
     * @property {string} author
     * @property {string} description
     * @property {string} name
     * @property {string} shortName
     * @property {string} version
     * @property {string} id
     * @property {string} authorEmail
     * @property {string} authorHref
     * @property {number} width
     * @property {number} height
     * @property {*} preferences
     * @see http://www.w3.org/TR/widgets-apis/
     */
    function Widget() {
        var self = this;
        ax.util.ajax({ url:g._APPSPRESSO_CONFIG_XML_URL||CONFIG_XML_URL, method:'GET', async:false, onload:function(xhr) {
            var xmlDoc = xhr.responseXML || ax.util.parseXML(xhr.responseText);
            var rootNode = xmlDoc.documentElement;

            var id = rootNode.getAttribute('id');
            var version = rootNode.getAttribute('version');
            var width = parseInt(rootNode.getAttribute('width'), 10);
            var height = parseInt(rootNode.getAttribute('height'), 10);

            var name, shortName;
            var nameNodes = rootNode.getElementsByTagName('name');
            if(nameNodes && nameNodes.length > 0) {
                name = nameNodes[0].textContent;
                shortName = nameNodes[0].getAttribute('short');
            }

            var author, authorEmail, authorHref;
            var authorNodes = rootNode.getElementsByTagName('author');
            if(authorNodes && authorNodes.length > 0) {
                author = authorNodes[0].textContent;
                authorEmail = authorNodes[0].getAttribute('email');
                authorHref = authorNodes[0].getAttribute('href');
            }

            var description;
            var descriptionNodes = rootNode.getElementsByTagName('description');
            if(descriptionNodes && descriptionNodes.length > 0) {
                description = descriptionNodes[0].textContent;
            }

            // TODO: l10n support... author, version, shortName, name, description

            // TODO: ...

            /////////////////////////////////////////////////////////////////////////
            // preferences
            (function(widget){
                // function returnUndefined() {
                //     return undefined;
                // }
                //
                // function setPropertyMethod(prop) {
                //     ax.def(preferences).property(prop,
                //             function() {
                //         var ret = getItem.call(preferences, prop);
                //         return ret === "" || !!ret ? ret : undefined;
                //     },
                //     function(value) { setItem.call(preferenc, prop, value) });
                // }
                //
                // function removePropertyMethod(prop) {
                //     ax.def(preferences).property(prop, returnUndefined, returnUndefined);
                // }

                function length() {
                    return this.execSync('length', []);
                }
                function key(index) {
                    return this.execSync('key', [ index ]);
                }
                function getItem(key) {
                    var ret = this.execSync('getItem', [ key ]);
                    return ret;
                }
                function setItem(key, value) {
                    this.execSync('setItem', [ key, value ]);
                }
                function removeItem(key) {
                    var ret = this.execSync('removeItem', [ key ]);
                    return ret;
                }
                function clear() {
                    this.execSync('clear', []);
                }

                var preferences =
                    ax.def(ax.plugin('ax.w3.widget.preferences', {}))
                        .property('length', length)
                        .method('key', key)
                        .method('getItem', getItem)
                        .method('setItem', setItem)
                        .method('removeItem', removeItem)
                        .method('clear', clear)
                        .end();

                ax.def(widget).constant('preferences', preferences);
            })(self);

            ax.def(self)
                .constant('author', author||'')
                .constant('description', description||'')
                .constant('name', name||'')
                .constant('shortName', shortName||'')
                .constant('version', version||'')
                .constant('id', id||'')
                .constant('authorEmail', authorEmail||'')
                .constant('authorHref', authorHref||'')
                .constant('width', width)
                .constant('height', height);
        }, onerror:function(xhr, e) {
            throw ax.error(ax.UNEXPECTED_ERR, "failed to parse widget config", e);
        }});
    }

    function getWidget() {
        if(!widget) {
            widget = new Widget();
        }
        return widget;
    }

    ax.def(g).property('widget', getWidget);
    // ====================================================
}(window));
