TestCase('deviceapis', {
    test_deviceapis: function() {
        assertTrue(window.hasOwnProperty('StringArray'));
        assertTrue(window.hasOwnProperty('UnsignedShortArray'));
        assertTrue(window.hasOwnProperty('ShortArray'));
        assertTrue(window.hasOwnProperty('FeatureArray'));
        assertTrue(window.hasOwnProperty('Feature'));
        assertTrue(window.hasOwnProperty('PendingOperation'));
        assertTrue(window.hasOwnProperty('DeviceAPIError'));
        assertTrue(window.hasOwnProperty('Deviceapis'));
        assertTrue(window.hasOwnProperty('deviceapis'));
        assertTrue(window.deviceapis instanceof Deviceapis);
    },

    test_createDeviceAPIError: function() {

        var err = ax.util.createDeviceAPIError(123, 'message');
        assertTrue(err instanceof window.DeviceAPIError);
        assertTrue(ax.isError(err));
        assertEquals(123, err.code);
        assertEquals('message', err.message);

    },

    test_validateParamWAC: function() {
        //TODO:
        //assertTrue(ax.util.validateParamWAC());
    },
    test_validateInstanceWAC: function() {
        //TODO:
        //assertTrue(ax.util.validateInstanceWAC());
    },

    test_validateCallback: function() {
        //TODO:
        //assertTrue(ax.util.validateCallback());
        var scb = function(){};
        var ecb = function(e){ alert(e.message); };
        var ecbCommon = function(){
                    ax.console.warn('error callback was ignored from "' + name + '" method');
                };

        try{
            ax.util.validateCallback(null,ecb,'testFunction1');
        }catch(e){
            assertSame('must thrown DeviceAPIError', e.code, 17);
        }
        assertSame('valid error callback', ecb, ax.util.validateCallback(scb,ecb,'testFunction'));

        try{
            ax.util.validateCallback(scb,'[]','testFunction3').toString();
        }catch(e){
            assertSame('must thrown DeviceAPIError', e.code, 17);
        }

        try{
            ax.util.validateCallback(scb,'{}','testFunction3').toString();
        }catch(e){
            assertSame('must thrown DeviceAPIError', e.code, 17);
        }

        try{
            ax.util.validateCallback(scb,undefined,'testFunction3').toString();
        }catch(e){
            assertSame('must thrown DeviceAPIError', e.code, 17);
        }

        try{
            ax.util.validateCallback(scb,null,'testFunction3').toString();
        }catch(e){
            assertSame('must thrown DeviceAPIError', e.code, 17);
        }

        assertFunction('null err callback',  ax.util.validateCallback(scb,null,'testFunction2'));
        assertFunction('undefined err callback', ax.util.validateCallback(scb,undefined,'testFunction3'));

    },

    test_mixins_AxPlugin: function() {
        assertTrue(ax.isFunction(ax.AxPlugin.prototype.execSyncWAC));
        assertTrue(ax.isFunction(ax.AxPlugin.prototype.execAsyncWAC));
        assertTrue(ax.isFunction(ax.AxPlugin.prototype.watchWAC));
        assertTrue(ax.isFunction(ax.AxPlugin.prototype.stopWatchWAC));

        function sync_ok() {
            _APPSPRESSO_REQUEST_URL = '/test/src-test/result.json';
            _APPSPRESSO_REQUEST_METHOD = 'GET';
            var result = this.execSyncWAC('sync_ok', [ 'param1', 2 ]);
            jstestdriver.console.log('sync_ok: result=' + ax.util.dump(result));
        }

        function sync_err() {
            _APPSPRESSO_REQUEST_URL = '/test/src-test/error.json';
            _APPSPRESSO_REQUEST_METHOD = 'GET';
            try {
                this.execSyncWAC('sync_err', [ 'param1', 2 ]);
            } catch(error) {
                assertTrue(error instanceof DeviceAPIError);
                jstestdriver.console.log('sync_err: error=' + ax.util.dump(error));
                return;
            }
        }

        function async_cb(result) {
            jstestdriver.console.log('async_cb: result=' + ax.util.dump(result));
        }

        function async_eb(error) {
            assertTrue(error instanceof DeviceAPIError);
            jstestdriver.console.log('async_eb: error=' + ax.util.dump(error));
        }

        function async_ok() {
            _APPSPRESSO_REQUEST_URL = '/test/src-test/result.json';
            _APPSPRESSO_REQUEST_METHOD = 'GET';
            var po = this.execAsyncWAC('async_ok', async_cb, async_eb, [ 'param1', 2 ]);
            assertTrue(po instanceof PendingOperation);
        }

        function async_err() {
            _APPSPRESSO_REQUEST_URL = '/test/src-test/error.json';
            _APPSPRESSO_REQUEST_METHOD = 'GET';
            var po = this.execAsyncWAC('async_err', async_cb, async_eb, [ 'param1', 2 ]);
            assertTrue(po instanceof PendingOperation);
        }

        function watch_ok() {
            // TODO: ...
            //this.watchWAC();
        }

        function stopwatch_ok() {
            // TODO: ...
            //this.stopWatchWAC();
        }

        var plugin1 = ax.plugin('plugin1', {
            'sync_ok': sync_ok,
            'sync_err': sync_err,
            'async_ok': async_ok,
            'async_err': async_err,
            'watch_ok': watch_ok,
            'stopwatch_ok': stopwatch_ok
        });

        assertTrue(ax.isPlugin(plugin1));

        plugin1.sync_ok();
        plugin1.sync_err();
        plugin1.async_ok();
        plugin1.async_err();
    },

    test_listAvailableFeatures: function() {

        var deviceapis = window.deviceapis;
        assertNotNull(deviceapis);


        _APPSPRESSO_REQUEST_URL = '/test/src-test/features_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        var features = deviceapis.listAvailableFeatures();
        jstestdriver.console.log(ax.util.dump(features));

        assertNotNull(features);
        assertEquals(2, features.length);

        assertEquals('http://feature1', features[0].uri);
        assertEquals(true, features[0].required);
        assertEquals('value1', features[0].params.param1);
        assertEquals('value2', features[0].params.param2);

        assertEquals('http://feature2', features[1].uri);
        assertEquals(false, features[1].required);
        assertEquals({}, features[1].params);

    },

    test_listActivatedFeatures: function() {
        var deviceapis = window.deviceapis;
        assertNotNull(deviceapis);


        _APPSPRESSO_REQUEST_URL = '/test/src-test/features_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        var features = deviceapis.listActivatedFeatures();
        jstestdriver.console.log(ax.util.dump(features));

        assertNotNull(features);
        assertEquals(2, features.length);

        assertEquals('http://feature1', features[0].uri);
        assertEquals(true, features[0].required);
        assertEquals('value1', features[0].params.param1);
        assertEquals('value2', features[0].params.param2);

        assertEquals('http://feature2', features[1].uri);
        assertEquals(false, features[1].required);
        assertEquals({}, features[1].params);
    }

});