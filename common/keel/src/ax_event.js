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
