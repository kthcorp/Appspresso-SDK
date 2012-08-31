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
