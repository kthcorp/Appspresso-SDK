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
