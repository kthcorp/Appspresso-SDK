var RESULT_URL = '/test/src-test/res1.json';
var ERROR_URL = '/test/src-test/err1.json';
var ASYNCRPC_URL = '/test/src-test/asyncrpc.json';

_APPSPRESSO_REQUEST_METHOD = 'GET';

var REQID = 1;

var Plugin1 = {
    sync : function(a, b, c) {
        console.log('***Plugin1.sync');
        _APPSPRESSO_REQUEST_URL = RESULT_URL;
        return this.execSync('sync', [ a, b, c ]);
    },
    sync_error : function(a, b, c) {
        console.log('***Plugin1.sync_error');
        _APPSPRESSO_REQUEST_URL = ERROR_URL;
        return this.execSync('sync_error', [ a, b, c ]);
    },
    async: function(cb, eb, a, b, c) {
        console.log('***Plugin1.async');
        _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
        this.execAsync('async', cb, eb, [ a, b, c ], REQID);
        _APPSPRESSO_RPCPOLL_URL = RESULT_URL;
        ax.bridge.rpcpoll._once();
    },
    async_error : function(cb, eb, a, b, c) {
        console.log('***Plugin1.async_error');
        _APPSPRESSO_REQUEST_URL = ASYNCRPC_URL;
        this.execAsync('async_error', cb, eb, [ a, b, c ], REQID);
        _APPSPRESSO_RPCPOLL_URL = ERROR_URL;
        ax.bridge.rpcpoll._once();
    }
};

TestCase('ax_plugin', {
    test_ax_plugin: function(){
        //ax.plugin 으로 플러그인 객체를 생성
        var plugin1 = ax.plugin('plugin1', Plugin1);
        assertTrue(ax.isPlugin(plugin1));
    },
    test_execSync: function(){
        //AxPlugin.execSync() 성공하면 (동기) 결과 리턴
        var plugin1 = ax.plugin('plugin1', Plugin1);

        var ret = plugin1.sync(1, 2, 3);
        assertEquals('result', ret);
    },
    test_execSync_error: function(){
        //AxPlugin.execSync() 실패하면 (동기) 예외 던짐
        var plugin1 = ax.plugin('plugin1', Plugin1);

        try {
            plugin1.sync_error(1, 2, 3);
            fail();
        } catch (err) {
            assertTrue(ax.isError(err));
            assertEquals(1, err.code);
            assertEquals('error', err.message);
        }
    }
});

AsyncTestCase('ax_plugin_async', {
    test_execAsync: function(q){
        //AxPlugin.execAsync() 성공하면 결과를 인자로 콜백함수 (비동기) 호출
        var plugin1 = ax.plugin('plugin1', Plugin1);

        var state = false;
        q.defer(function(pool) {
            function cb(ret) {
                assertEquals('result', ret);
                state = true;
            }
            function eb(err) {
            }
            plugin1.async(pool.add(cb), pool.addErrback(eb), 1, 2, 3);
        });
        q.defer(function(){
            assertTrue(state);
        });
    },
    test_execAsync_error: function(q){
        //AxPlugin.execAsync() 실패하면 에러를 인자로 에러콜백함수 (비동기) 호출
        var plugin1 = ax.plugin('plugin1', Plugin1);

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
            plugin1.async_error(pool.addErrback(cb), pool.add(eb), 1, 2, 3);
        });
        q.defer(function(){
            assertTrue(state);
        });
    }
});
