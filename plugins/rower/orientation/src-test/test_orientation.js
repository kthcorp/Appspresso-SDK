var eclipseTesting = AsyncTestCase('eclipseTesting');

eclipseTesting.prototype.test_with_eclipse_external_tool = function(queue) {
    try{

    }catch(e){
        alert(e.message);
    }
};

var fsAsync = AsyncTestCase('fsAsync');

fsAsync.prototype.test_watch_orientation_with_options = function(queue){

    var scb, ecb;

    queue.call('Step 1: watchOrientation with options', function(callbacks) {
        console.log('================== Step 2: watchOrientation with options ======================================================');

        _APPSPRESSO_REQUEST_URL = '/test/src-test/response_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        scb = function(o){
            console.log("OK watchOrientation scb ",o.beta, o.alpha, o.gamma );
            assertEquals('beta', -3, o.beta);
            assertEquals('alpha', 82, o.alpha);
            assertEquals('gamma', 0, o.gamma);

        };
        ecb = undefined;

        var options = 'string';

        try{
            deviceapis.orientation.watchOrientation(scb, ecb, options);
        }catch(e){ console.log("OK watchOrientation with <<string>> options", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        options = {'minNotificationInterval':'string'};
        try{
            deviceapis.orientation.watchOrientation(scb, ecb, options);
        }catch(e){ console.log("OK watchOrientation with <<string>> options", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

        options = {'minNotificationInterval':1000};

        var watchID = deviceapis.orientation.watchOrientation(scb, ecb, options);

        setTimeout(function(){
            deviceapis.orientation.clearWatch(watchID);
        }, 5000);
    });

};

fsAsync.prototype.test_watch_orientation_clear_watch = function(queue){
    var scb, ecb, watchID;
    queue.call('Step 1: watchOrientation', function(callbacks) {
        console.log('================== Step 1: watchOrientation  ======================================================');

        scb = function(o){
            console.log("OK watchOrientation scb ",o.beta, o.alpha, o.gamma );
            assertEquals('beta', -3, o.beta);
            assertEquals('alpha', 82, o.alpha);
            assertEquals('gamma', 0, o.gamma);

        };
        ecb = undefined;

        _APPSPRESSO_REQUEST_URL = '/test/src-test/response_ok.json';
        watchID = deviceapis.orientation.watchOrientation(scb, ecb);

        setTimeout(function(){
            deviceapis.orientation.clearWatch(watchID);
        }, 2000);
    });
}

fsAsync.prototype.test_watch_orientation_clear_watch_force_ecb = function(queue){
        queue.call('Step 1: watchOrientation', function(callbacks) {
            console.log('================== Step 1: watchOrientation & clearWatch param validate ======================================================');

            var scb, ecb, watchID;

            try{
                deviceapis.orientation.watchOrientation(scb, ecb);
            }catch(e){ console.log("OK getCurrentOrientation with <<undefined>> scb", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


            _APPSPRESSO_REQUEST_URL = '/test/src-test/general_error.json';
            _APPSPRESSO_REQUEST_METHOD = 'GET';

            try{
                deviceapis.orientation.clearWatch(undefined);
            }catch(e){ console.log("OK clearWatch with invalid watchID", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

            try{
                deviceapis.orientation.clearWatch(100);
            }catch(e){ console.log("OK clearWatch with invalid watchID", e.message, e.code); assertSame('INVALID_VALUE_ERR', 22, e.code); }


            scb = function(o){};
            ecb = function(e){  console.log("OK watchOrientation with valid ecb (force ecb)", e.message, e.code); assertSame('GENERAL_ERROR_FOR_TESTING', 201, e.code); };

            watchID = deviceapis.orientation.watchOrientation(scb, ecb);
            console.log('watchID: ' + watchID);


            setTimeout(function(){
                deviceapis.orientation.clearWatch(watchID);
            }, 2000);

        });
};

fsAsync.prototype.test_getCurrentOrientation = function(queue){

    var scb, ecb;
    queue.call('Step 1: getCurrentOrientation param validate', function(callbacks) {
        console.log('================== Step 1: getCurrentOrientation param validate ======================================================');

        try{
            deviceapis.orientation.getCurrentOrientation(scb, ecb);
        }catch(e){ console.log("OK getCurrentOrientation with <<undefined>> scb", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        _APPSPRESSO_REQUEST_URL = '/test/src-test/general_error.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        scb = function(o){};
        ecb = function(e){  console.log("OK getCurrentOrientation with valid ecb (force ecb)", e.message, e.code); assertSame('GENERAL_ERROR_FOR_TESTING', 201, e.code); };

        deviceapis.orientation.getCurrentOrientation(scb, ecb);

        ecb = undefined;
        console.log("CHECK: IGNORE ERROR LOG: Warning Log should shown with 'error callback was ignored: error=AxError: GENERAL_ERROR_FOR_TESTING, code=[201: ], cause=[]'");
        deviceapis.orientation.getCurrentOrientation(scb, ecb);
    });


    queue.call('Step 2: getCurrentOrientation', function(callbacks) {
        console.log('================== Step 2: getCurrentOrientation ======================================================');

        try{
            deviceapis.orientation.getCurrentOrientation(scb, ecb);
        }catch(e){ console.log("OK getCurrentOrientation with <<undefined>> scb", e.message, e.code); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        _APPSPRESSO_REQUEST_URL = '/test/src-test/response_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        scb = function(o){
            console.log("OK getCurrentOrientation scb ",o.beta, o.alpha, o.gamma );
            assertEquals('beta', -3, o.beta);
            assertEquals('alpha', 82, o.alpha);
            assertEquals('gamma', 0, o.gamma);

        };
        ecb = undefined;

        deviceapis.orientation.getCurrentOrientation(scb, ecb);
    });


};

