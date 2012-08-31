var RESULT_URL = '/test/src-test/res1.json';
var ERROR_URL = '/test/src-test/err1.json';
var ASYNCRPC_URL = '/test/src-test/asyncrpc.json';

_APPSPRESSO_REQUEST_METHOD = 'GET';

var REQID = 1;

function rpc(jsobj) {
    ax.bridge.jsonrpc(JSON.stringify(jsobj));
}

TestCase('ax_bridge', {
    test_ax_bridge: function() {
        //ax 의 bridge 속성으로 접근
        assertTrue(ax.hasOwnProperty('bridge'));
    },
    test_execSync: function() {
        //ax.bridge.execSync() 성공하면 (동기) 결과 리턴
        _APPSPRESSO_REQUEST_URL = RESULT_URL;

        var ret = ax.bridge.execSync('method', [ 1, 2, 3 ]);
        assertEquals('result', ret);
    },
    test_execSync_error: function() {
        //ax.bridge.execSync() 실패하면 (동기) 예외 던짐
        _APPSPRESSO_REQUEST_URL = ERROR_URL;

        try {
            ax.bridge.execSync('method', [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(1, e.code);
            assertEquals('error', e.message);
        }
    },
    test_execSync_no_method: function(q) {
        try {
            ax.bridge.execSync(null, [1, 2, 3]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execSync(undefined, [1, 2, 3]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execSync(0, [1, 2, 3]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execSync(123, [1, 2, 3]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
    },
    test_execSync_no_params: function(q) {
        try {
            ax.bridge.execSync('method', null);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execSync('method', undefined);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execSync('method', 0);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execSync('method', 123);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
    },
    test_execAsync_no_method: function(q) {
        try {
            ax.bridge.execAsync(null, function() {}, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync(undefined, function() {}, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync(0, function() {}, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync(123, function() {}, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
    },
    test_execAsync_no_params: function(q) {
        try {
            ax.bridge.execAsync('method', function() {}, function() {}, null);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync('method', function() {}, function() {}, undefined);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync('method', function() {}, function() {}, 0);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync('method', function() {}, function() {}, 123);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
    },
    test_execAsync_no_callback: function(q) {
        try {
            ax.bridge.execAsync('method', null, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
        }
        try {
            ax.bridge.execAsync('method', undefined, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
        }
        try {
            ax.bridge.execAsync('method', 0, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
        }
        try {
            ax.bridge.execAsync('method', 123, function() {}, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
        }
    },
    test_execAsync_no_errback: function(q) {
        try {
            ax.bridge.execAsync('method', function() {}, null, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync('method', function() {}, undefined, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync('method', function() {}, 0, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
        try {
            ax.bridge.execAsync('method', function() {}, 123, [ 1, 2, 3 ]);
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(ax.TYPE_MISMATCH_ERR, e.code);
        }
    },

    test_session: function() {
        var id = ax.bridge.session();
        jstestdriver.console.log('bridge session id:', id);
        assertNumber(id);
    },

    test_jsonrpc: function() {
        var cnt = 0;
        function cb(ret) {
            assertEquals('result', ret);
            cnt++;
        }
        function eb(err) { }
        _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
        var op = ax.bridge.execAsync('method', cb, eb, [], REQID);
        assertObject(op);

        var jsobj = {
            id: REQID,
            result: 'result',
            error: null
        };

        rpc(jsobj);
        assertEquals(cnt, 1);

        rpc(jsobj);
        assertEquals(cnt, 1);
    },

    test_jsonrpc_plural: function() {
        var cnt = 0;
        function cb(ret) {
            assertEquals('result', ret);
            cnt++;
        }
        function eb(err) {
            assertEquals('error', err.message);
            cnt += 100;
        }
        _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
        var op = ax.bridge.execAsync('method', cb, eb, [], REQID);
        var op2 = ax.bridge.execAsync('method2', cb, eb, [], REQID + 1);
        assertObject(op);
        assertObject(op2);

        var jsobj = {
            id: null,
            method: 'ax.bridge.jsonrpc.plural',
            params: []
        };
        jsobj.params.push({
            id: REQID,
            result: 'result',
            error: null
        });
        jsobj.params.push({
            id: REQID + 1,
            result: null,
            error: { code: 1, message: 'error' }
        });

        rpc(jsobj);
        assertEquals(cnt, 101);

        rpc(jsobj);
        assertEquals(cnt, 101);
    },

    test_jsonrpc_event_trigger: function() {
        var val = 0;
        ax.event.on('foo', function(p1, p2) {
            val += p1 + p2;
        });

        var evtobj = {
            id: null,
            method: 'ax.event.trigger',
            params: [{
                type: 'foo',
                params: [2, 3]
            }]
        };

        rpc(evtobj);
        assertEquals(val, 5);
        rpc(evtobj);
        assertEquals(val, 10);

        ax.event.off('foo');
        rpc(evtobj);
        assertEquals(val, 10);
    },

    test_jsonrpc_eval: function() {
        var state = false;
        window.foo = function() { state = true; };
        window.bar = function() { throw { code: 42, message: 'answer' }; };

        rpc({
            id: null,
            method: 'ax.bridge.eval',
            params: ['window.foo()']
        });

        assertTrue(state);

        rpc({
            id: null,
            method: 'ax.bridge.eval',
            params: ['window.bar()']
        });

        // eval 안쪽에서 예외 발생시켜도 ax.bridge 내에서 처리되어야 한다.
    },

    test_watch: function() {
        var cnt = 0;
        function cb(res) {
            assertEquals('result', res);
            cnt++;
        }
        function eb(err) {
            assertEquals('error', err.message);
            cnt += 100;
        }

        _APPSPRESSO_REQUEST_URL = RESULT_URL;
        var watchID = ax.bridge.watch('method', cb, eb, []);

        var sample = {
            method: 'ax.watch.sample',
            params: [{
                id: watchID,
                result: 'result',
                error: null
            }],
            id: null
        }, errorSample = {
            method: 'ax.watch.sample',
            params: [{
                id: watchID,
                result: null,
                error: { code: 1, message: 'error' }
            }],
            id: null
        };

        rpc(sample);
        assertEquals(cnt, 1);

        rpc(errorSample);
        assertEquals(cnt, 101);

        rpc(sample);
        rpc(sample);
        rpc(errorSample);
        rpc(sample);
        assertEquals(cnt, 204);

        ax.bridge.stopWatch('method', watchID);

        rpc(sample);
        rpc(errorSample);
        assertEquals(cnt, 204);
    }

});

AsyncTestCase('ax_bridge_async', {
    test_execAsync: function(q) {
        //ax.bridge.execAsync() 성공하면 결과를 인자로 콜백함수 (비동기) 호출

        var state = false;
        q.defer(function(pool) {
            function cb(ret) {
                assertEquals('result', ret);
                state = true;
            }
            function eb(err) {
            }
            _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
            var op = ax.bridge.execAsync('method', pool.add(cb), pool.addErrback(eb), [ 1, 2, 3 ], REQID);
            assertObject(op);
            _APPSPRESSO_RPCPOLL_URL = RESULT_URL;
            ax.bridge.rpcpoll._once();
        });
        q.defer(function() {
            assertTrue(state);
        });
    },
    test_execAsync_error: function(q) {
        //ax.bridge.execAsync() 실패하면 에러를 인자로 에러콜백함수 (비동기) 호출
        _APPSPRESSO_REQUEST_URL = ERROR_URL;

        var state = false;
        q.defer(function(pool) {
            function cb(ret) {
            }
            function eb(err) {
                assertTrue(ax.isError(err));
                assertEquals(1, err.code);
                assertEquals('error', err.message);
                state = true;
            }
            _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
            var op = ax.bridge.execAsync('method', pool.addErrback(cb), pool.add(eb), [ 1, 2, 3 ], REQID);
            assertObject(op);
            _APPSPRESSO_RPCPOLL_URL = ERROR_URL;
            ax.bridge.rpcpoll._once();
        });
        q.defer(function() {
            assertTrue(state);
        });
    }
});
