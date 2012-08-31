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
