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
