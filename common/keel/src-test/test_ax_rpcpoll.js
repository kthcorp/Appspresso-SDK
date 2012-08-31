var RESULT_URL = '/test/src-test/res1.json';
var ERROR_URL = '/test/src-test/err1.json';

// for ax_request
_APPSPRESSO_REQUEST_METHOD = 'GET';
_APPSPRESSO_REQUEST_URL = '/test/src-test/asyncrpc.json';

TestCase('ax_rpcpoll', {

    test_rpcpoll: function() {
        assertTrue(ax.bridge.hasOwnProperty('rpcpoll'));
    }

});

AsyncTestCase('ax_rpcpoll_async', {

    test_success: function(q) {
        var state = false,
            REQID = 1;

        q.defer(function(pool) {
            function cb(ret) {
                assertEquals('result', ret);
                state = true;
            }
            function eb(err) { }
            var op = ax.bridge.execAsync('method', pool.add(cb), pool.addErrback(eb), [1, 2, 3], REQID);
            _APPSPRESSO_RPCPOLL_URL = RESULT_URL;
            ax.bridge.rpcpoll._once();
        });
        q.defer(function() {
            assertTrue(state);
        });
    },

    test_error: function(q) {
        var state = false,
            REQID = 1;

        q.defer(function(pool) {
            function cb(ret) { }
            function eb(err) {
                assertTrue(ax.isError(err));
                assertEquals(1, err.code);
                assertEquals('error', err.message);
                state = true;
            }
            var op = ax.bridge.execAsync('method', pool.addErrback(cb), pool.add(eb), [1, 2, 3], REQID);
            _APPSPRESSO_RPCPOLL_URL = ERROR_URL;
            ax.bridge.rpcpoll._once();
        });
        q.defer(function() {
            assertTrue(state);
        });

    }

});
