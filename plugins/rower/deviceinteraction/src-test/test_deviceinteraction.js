var eclipseTesting = AsyncTestCase('eclipseTesting');

eclipseTesting.prototype.test_with_eclipse_external_tool = function(queue) {

};

var fsAsync = AsyncTestCase('fsAsync');

fsAsync.prototype.test_setWallpaper = function(queue){
    _APPSPRESSO_REQUEST_METHOD = 'GET';
    _APPSPRESSO_REQUEST_URL = '/test/src-test/general_ok.json';

    queue.call('Step 0: deviceinteraction', function(callbacks) {
        var deviceinteraction = deviceapis.deviceinteraction;
        assertObject('deviceapis.deviceinteraction obj', deviceinteraction);
    });

    queue.call('Step 1: setWallpaper', function(callbacks) {
        console.log('================== 1. setWallpaper callback test ======================================================');

        var scb, ecb;

        scb = function(){
            fail('unexpected success callback');
            console.log('FAIL unexpected success callback');
        };

        ecb = function(e){
            fail('unexpected error callback');
            console.log('FAIL unexpected error callback', e.message, e.code);
        };

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-1 setWallpaper with scb <<undefined>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper(null, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-2 setWallpaper with scb <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper({}, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-3 setWallpaper with scb <<{}>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper([], ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-4 setWallpaper with scb <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, null); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-5 setWallpaper with ecb <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, []); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-6 setWallpaper with ecb <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, {}); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-7 setWallpaper with ecb <<{}>>", e.message, e.code);
        }

        console.log('================== 2. startNotify fileName test ======================================================');

        scb = function(){
            fail('unexpected success callback');
        };

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, ecb, null); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-1 setWallpaper with fileName <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, ecb, []); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-2 setWallpaper with fileName <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, ecb, {}); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-3 setWallpaper with fileName <<{}>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.setWallpaper(scb, ecb, undefined); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-4 setWallpaper with fileName <<undefined>>", e.message, e.code);
        }

        var ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 2-5 setWallpaper with fileName <<../documents>> errorCallback", e.message, e.code);
        }
        deviceapis.deviceinteraction.setWallpaper(scb, callbacks.add(ecb), '../documents');

        var ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 2-6 setWallpaper with fileName <<documents/../images/>> errorCallback", e.message, e.code);
        }
        deviceapis.deviceinteraction.setWallpaper(scb, callbacks.add(ecb), 'documents/../images/');

        scb = callbacks.add(function(){
            console.log("OK 2-7 setWallpaper with fileName <<documents/test.jpg>> successCallbak");
        });
        deviceapis.deviceinteraction.setWallpaper(callbacks.add(scb), ecb, 'documents/test.jpg');

    });
};

fsAsync.prototype.test_startVibrate = function(queue){
    _APPSPRESSO_REQUEST_METHOD = 'GET';
    _APPSPRESSO_REQUEST_URL = '/test/src-test/general_ok.json';

    queue.call('Step 0: deviceinteraction', function(callbacks) {
        var deviceinteraction = deviceapis.deviceinteraction;
        assertObject('deviceapis.deviceinteraction obj', deviceinteraction);
    });

    queue.call('Step 1: startVibrate', function(callbacks) {
        console.log('================== 1. startVibrate callback test ======================================================');

        var scb, ecb;

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-1 startVibrate with scb <<undefined>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate(null, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-2 startVibrate with scb <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate({}, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-3 startVibrate with scb <<{}>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate([], ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-4 startVibrate with scb <<[]>>", e.message, e.code);
        }

        scb = function(){
            fail('unexpected success callback');
        };

        try {
            deviceapis.deviceinteraction.startVibrate(scb, null); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-5 startVibrate with ecb <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate(scb, []); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-6 startVibrate with ecb <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify(scb, {}); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-7 startVibrate with ecb <<{}>>", e.message, e.code);
        }

        console.log('================== 2. startVibrate duration test ======================================================');

        scb = function(){
            fail('unexpected success callback');
        };

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb, null); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-1 startVibrate with duration <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb, []); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-2 startVibrate with duration <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb, {}); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-3 startVibrate with duration <<{}>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb, undefined); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-4 startVibrate with duration <<undefined>>", e.message, e.code);
        }

        scb = function(){
            console.log("OK 2-5 startVibrate with duration 100 successCallbak");
        };
        deviceapis.deviceinteraction.startVibrate(callbacks.add(scb), ecb, 100);


        console.log('================== 3. startVibrate pattern test ======================================================');

        scb = function(){
            fail('unexpected success callback');
        };

        var ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 3-1 startVibrate with pattern <<hello>>", e.message, e.code);
        };
        deviceapis.deviceinteraction.startVibrate(scb, ecb, null, 'hello');

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb, null, []); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 3-2 startVibrate with pattern <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb, null, {}); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 3-3 startVibrate with pattern <<{}>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startVibrate(scb, ecb, null, undefined); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 3-4 startVibrate with pattern <<undefined>>", e.message, e.code);
        }

        scb = function(){
            console.log("OK 3-5 startVibrate with pattern '_._.'  successCallbak");
        };
        deviceapis.deviceinteraction.startVibrate(callbacks.add(scb), ecb, 10000, '_._.');

        try{
            deviceapis.deviceinteraction.stopVibrate();
        }catch(e){
            fail('unexpected error thrown');
            console.log("FAIL: unexpected error thrown'", e.message, e.code);
        }
    });
};

fsAsync.prototype.test_startNotify = function(queue){
    _APPSPRESSO_REQUEST_METHOD = 'GET';
    _APPSPRESSO_REQUEST_URL = '/test/src-test/general_ok.json';

    queue.call('Step 0: deviceinteraction', function(callbacks) {
        var deviceinteraction = deviceapis.deviceinteraction;
        assertObject('deviceapis.deviceinteraction obj', deviceinteraction);
    });

    queue.call('Step 1: startNotify', function(callbacks) {
        console.log('================== 1. startNotify callback test ======================================================');

        var scb, ecb;

        try {
            deviceapis.deviceinteraction.startNotify(scb, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-1 startNotify with scb <<undefined>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify(null, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-2 startNotify with scb <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify({}, ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-3 startNotify with scb <<{}>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify([], ecb); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-4 startNotify with scb <<[]>>", e.message, e.code);
        }

        scb = function(){
            fail('unexpected success callback');
        };

        try {
            deviceapis.deviceinteraction.startNotify(scb, null); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-5 startNotify with ecb <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify(scb, []); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-6 startNotify with ecb <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify(scb, {}); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-7 startNotify with ecb <<{}>>", e.message, e.code);
        }

        console.log('================== 2. startNotify duration test ======================================================');

        scb = function(){
            fail('unexpected success callback');
        };

        try {
            deviceapis.deviceinteraction.startNotify(scb, ecb, null); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-1 startNotify with duration <<null>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify(scb, ecb, []); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-2 startNotify with duration <<[]>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify(scb, ecb, {}); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-3 startNotify with duration <<{}>>", e.message, e.code);
        }

        try {
            deviceapis.deviceinteraction.startNotify(scb, ecb, undefined); fail('must throw err');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-4 startNotify with duration <<undefined>>", e.message, e.code);
        }

        scb = callbacks.add(function(){
            console.log("OK 2-5 startNotify with duration 100 successCallbak");
        });
        deviceapis.deviceinteraction.startNotify(callbacks.add(scb), ecb, 100);

        try{
            deviceapis.deviceinteraction.stopNotify();
        }catch(e){
            fail('unexpected error thrown');
            console.log("FAIL: unexpected error thrown'", e.message, e.code);
        }
    });
};
