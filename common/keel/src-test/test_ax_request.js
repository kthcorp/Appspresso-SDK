var RESULT_URL = '/test/src-test/res1.json';
var ERROR_URL = '/test/src-test/err1.json';
var NOTFOUND_URL = '/__not_found__';
var ASYNCRPC_URL = '/test/src-test/asyncrpc.json';

_APPSPRESSO_REQUEST_METHOD = 'GET';

var REQID = 1;

TestCase('ax_request', {

    test_ax_request: function() {
        //ax.request 로 요청 생성
        var req = ax.request('method', [ 1, 2, 3 ], REQID);
        assertTrue(ax.isRequest(req));
    },

    test_cancel: function() {
        var req = ax.request('method', [ 1, 2, 3 ], REQID);
        assertFalse(req.cancel());
    },

    test_doSync: function() {
        //doSync() 성공하면 (동기) 결과 리턴
        _APPSPRESSO_REQUEST_URL = RESULT_URL;

        var ret = ax.request('method', [ 1, 2, 3 ], REQID).doSync();
        assertEquals('result', ret);
    },

    test_doSync_error: function() {
        //doSync() 실패하면 (동기) 예외 던짐
        _APPSPRESSO_REQUEST_URL = ERROR_URL;

        try {
            ax.request('method', [ 1, 2, 3 ], REQID).doSync();
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(1, e.code);
            assertEquals('error', e.message);
        }
    },

    test_doSync_404: function() {
        //doSync() 실패하면 (동기) 예외 던짐
        _APPSPRESSO_REQUEST_URL = NOTFOUND_URL;

        try {
            ax.request('method', [ 1, 2, 3 ], REQID).doSync();
        } catch (e) {
            assertTrue(ax.isError(e));
            assertEquals(0, e.code);
        }
    },

    test_doSync_cancel: function() {
        //doSync() 실패하면 (동기) 예외 던짐
        _APPSPRESSO_REQUEST_URL = NOTFOUND_URL;

        var req = ax.request('method', [ 1, 2, 3 ], REQID);
        try {
            req.doSync();
        } catch(e) {
        }
        assertFalse(req.cancel());
    },

});

AsyncTestCase('ax_request_async', {
    test_doAsync: function(q) {
        //doAsync() 비동기 호출에는 jsonrpc 요청을 전단하는 것 만으로 doAsync의 역할은 끝.
        //응답은 longpoll 이나 sail에서 loadUrl 로 브릿지 호출하면서 이루어진다.

        var state = false;
        q.defer(function(pool) {
            function onload() {
                state = true;
            }
            _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
            ax.request('method', [ 1, 2, 3 ], REQID).doAsync(null, null, pool.add(onload));
        });
        q.defer(function() {
            assertTrue(state);
        });
    },

    test_doAsync_404: function(q) {
        //doAsync() 실패하면 에러를 인자로 에러콜백함수 (비동기) 호출

        var state = false;
        q.defer(function(pool) {
            function onerror(err) {
                jstestdriver.console.log(err.code);
                jstestdriver.console.log(err.message);
                state = true;
            }
            _APPSPRESSO_REQUEST_URL = NOTFOUND_URL;
            ax.request('method', [ 1, 2, 3 ], REQID).doAsync(null, pool.add(onerror));
        });
        q.defer(function() {
            assertTrue(state);
        });
    },

    test_doAsync_cancel: function(q) {

        var state = false;
        q.defer(function(pool) {
            _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
            var req = ax.request('method', [ 1, 2, 3 ], REQID).doAsync(function oncancel() {
                return (state = true);
            });
            assertTrue(req.cancel());
            assertTrue(state);
        });
    }

});
