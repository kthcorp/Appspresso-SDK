var eclipseTesting = AsyncTestCase('eclipseTesting');

eclipseTesting.prototype.test_with_eclipse_external_tool = function(queue) {
    try{

    }catch(e){
        alert(e.message);
    }
};

var fsAsync = AsyncTestCase('fsAsync');

fsAsync.prototype.test_watch_acceleration_with_options = function(queue){

    var scb, ecb;

    queue.call('Step 1: watchAcceleration with options', function(callbacks) {
        console.log('================== Step 2: watchAcceleration with options ======================================================');

        _APPSPRESSO_REQUEST_URL = '/test/src-test/response_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        scb = function(o){
            console.log("OK 2-3 watchAcceleration scb ",o.xAxis, o.zAxis, o.yAxis );
            assertEquals('xAxis', -0.3334261, o.xAxis);
            assertEquals('zAxis', -10.1989155, o.zAxis);
            assertEquals('yAxis', 0.17651969, o.yAxis);

        };
        ecb = undefined;

        var options = 'string';

        try{
            deviceapis.accelerometer.watchAcceleration(scb, ecb, options);
        }catch(e){ console.log("OK 2-1 watchAcceleration with <<string>> options", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        options = {'minNotificationInterval':'string'};
        try{
            deviceapis.accelerometer.watchAcceleration(scb, ecb, options);
        }catch(e){ console.log("OK 2-2 watchAcceleration with <<string>> options", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

        options = {'minNotificationInterval':1000};

        var watchID = deviceapis.accelerometer.watchAcceleration(callbacks.add(scb), ecb, options);

        setTimeout(function(){
            deviceapis.accelerometer.clearWatch(watchID);
        }, 5000);
    });

};

fsAsync.prototype.test_watch_acceleration_clear_watch = function(queue){

    queue.call('Step 1: watchAcceleration', function(callbacks) {
        console.log('================== Step 1: watchAcceleration  ======================================================');

        var scb, ecb, watchID;

        scb = function(o){
            console.log("OK watchAcceleration scb ",o.xAxis, o.zAxis, o.yAxis );
            assertEquals('xAxis', -0.3334261, o.xAxis);
            assertEquals('zAxis', -10.1989155, o.zAxis);
            assertEquals('yAxis', 0.17651969, o.yAxis);

        };
        ecb = undefined;

        _APPSPRESSO_REQUEST_URL = '/test/src-test/response_ok.json';
        watchID = deviceapis.accelerometer.watchAcceleration(callbacks.add(scb), ecb);

        setTimeout(function(){
            deviceapis.accelerometer.clearWatch(watchID);
        }, 2000);
    });
};

fsAsync.prototype.test_watch_acceleration_clear_watch_force_ecb = function(queue){

    queue.call('Step 1: watchAcceleration', function(callbacks) {
        console.log('================== Step 1: watchAcceleration & clearWatch param validate ======================================================');

        var scb, ecb, watchID;

        try{
            deviceapis.accelerometer.watchAcceleration(scb, ecb);
        }catch(e){ console.log("OK 1-1 getCurrentAcceleration with <<undefined>> scb", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        _APPSPRESSO_REQUEST_URL = '/test/src-test/general_error.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        try{
            deviceapis.accelerometer.clearWatch(undefined);
        }catch(e){ console.log("OK 1-2 clearWatch with invalid watchID", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

        try{
            deviceapis.accelerometer.clearWatch(100);
        }catch(e){ console.log("OK 1-3 clearWatch with invalid watchID", e.message, e.code); assertSame('INVALID_VALUE_ERR', 22, e.code); }


        scb = function(o){};
        ecb = function(e){  console.log("OK 1-4 watchAcceleration with valid ecb (force ecb)", e.message, e.code); assertSame('GENERAL_ERROR_FOR_TESTING', 201, e.code); };

        watchID = deviceapis.accelerometer.watchAcceleration(scb, callbacks.add(ecb));


        setTimeout(function(){
            deviceapis.accelerometer.clearWatch(watchID);
        }, 2000);

    });
};

fsAsync.prototype.test_getCurrentAcceleration = function(queue){

    var scb, ecb;
    queue.call('Step 1: getCurrentAcceleration param validate', function(callbacks) {
        console.log('================== Step 1: getCurrentAcceleration param validate ======================================================');

        try{
            deviceapis.accelerometer.getCurrentAcceleration(scb, ecb);
        }catch(e){ console.log("OK 1-1 getCurrentAcceleration with <<undefined>> scb", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        _APPSPRESSO_REQUEST_URL = '/test/src-test/general_error.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        scb = function(o){};
        ecb = function(e){  console.log("OK 1-2 getCurrentAcceleration with valid ecb (force ecb)", e.message, e.code); assertSame('GENERAL_ERROR_FOR_TESTING', 201, e.code); };

        deviceapis.accelerometer.getCurrentAcceleration(scb, callbacks.add(ecb));

        ecb = undefined;
        console.log("CHECK: IGNORE ERROR LOG: Warning Log should shown with 'error callback was ignored: error=AxError: GENERAL_ERROR_FOR_TESTING, code=[201: ], cause=[]'");
        deviceapis.accelerometer.getCurrentAcceleration(scb, ecb);
    });


    queue.call('Step 2: getCurrentAcceleration', function(callbacks) {
        console.log('================== Step 2: getCurrentAcceleration ======================================================');

        try{
            deviceapis.accelerometer.getCurrentAcceleration(scb, ecb);
        }catch(e){ console.log("OK 1-1 getCurrentAcceleration with <<undefined>> scb", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        _APPSPRESSO_REQUEST_URL = '/test/src-test/response_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        scb = function(o){
            console.log("OK 1-2 getCurrentAcceleration scb ",o.xAxis, o.zAxis, o.yAxis );
            assertEquals('xAxis', -0.3334261, o.xAxis);
            assertEquals('zAxis', -10.1989155, o.zAxis);
            assertEquals('yAxis', 0.17651969, o.yAxis);

        };
        ecb = undefined;

        deviceapis.accelerometer.getCurrentAcceleration(callbacks.add(scb), ecb);
    });


};

