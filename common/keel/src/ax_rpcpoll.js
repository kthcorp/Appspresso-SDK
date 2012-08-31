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
