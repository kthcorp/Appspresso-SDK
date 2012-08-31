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
