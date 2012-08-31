var eclipseTesting = AsyncTestCase('eclipseTesting');

eclipseTesting.prototype.test_with_eclipse_external_tool = function(queue) {

};

var fsAsync = AsyncTestCase('fsAsync');


fsAsync.prototype.test_messageObject = function(queue){
    var message;
    queue.call('Step 0: createMessage', function(callbacks) {
        message = deviceapis.messaging.createMessage(1);
        assertSame('instanceof Message', true, message instanceof Message);
    });

    queue.call('Step 1: message.body test ', function(callbacks) {
        console.log('================== Step 1: message.body test ======================================================');
        try {
            message.body = 123;
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-1 message.body with <<123>>", e.message, e.code);
        }

        try {
            message.body = false;
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-2 message.body with <<false>>", e.message, e.code);
        }

        try {
            message.body = undefined;
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-3 message.body with <<undefined>>", e.message, e.code);
        }

        try {
            message.body = null;
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-4 message.body with <<null>>", e.message, e.code);
        }

        message.body = 'hello';
        assertSame('message.body', 'hello', message.body);
        console.log("OK 1-4 message.body successed with <<hello>>");
    });

    queue.call('Step 2: message.to test ', function(callbacks) {
        console.log('================== Step 2: message.to test ======================================================');

        try {
            message.to = 123;
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-1 message.to with <<123>>", e.message, e.code);
        }

        try {
            message.to = {};
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-2 message.to with <<{}>>", e.message, e.code);
        }


        try {
            message.to = [01086491196];
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-3 message.to with <<[01086491196]>>", e.message, e.code);
        }

        try {
            message.to = ['01086491196',123456];
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 2-4 message.to with <<['01086491196',123456]>>", e.message, e.code);
        }

        message.to = ['01086491196','123456'];
        assertSame('message.to', true, message.to instanceof Array);
        assertSame('message.to', 2, message.to.length);
        console.log("OK 2-5 message.to successed with <<['01086491196','123456']>>");
    });

    queue.call('Step 3: message.type test ', function(callbacks) {
        console.log('================== Step 2: message.type test ======================================================');
        try {
            message.type = 2;
        } catch(e) {
            assertSame('NOT_SUPPORTED_ERR', 9, e.code);
            console.log("OK 3-1 message.type with <<2>>", e.message, e.code);
        }

        try {
            message.type = 3;
        } catch(e) {
            assertSame('NOT_SUPPORTED_ERR', 9, e.code);
            console.log("OK 3-2 message.type with <<3>>", e.message, e.code);
        }

        try {
            message.type = undefined;
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 3-3 message.type with <<undefined>>", e.message, e.code);
        }

        try {
            message.type = null;
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 3-4 message.type with <<null>>", e.message, e.code);
        }

        message.type = 1;
        assertSame('instanceof Message', true, message instanceof Message);
        console.log("OK 3-5 set type successed message type SMS(1)");
    });

    //TODO: 나머지 어트리뷰트는 지원안하는데 테스트 해야겠지?? - althjs

};

fsAsync.prototype.test_createMessage = function(queue){

    queue.call('Step 1: createMessage', function(callbacks) {
        console.log('================== Step 1: createMessage ======================================================');

        var messaging = deviceapis.messaging;
        assertObject('deviceapis.messaging obj', messaging);
        var message;

        try {
            message = messaging.createMessage(undefined);
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-1 createMessage with <<undefined>> message type", e.message, e.code);
        }
        try {
            message = messaging.createMessage(null);
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-2 createMessage with <<null>> message type", e.message, e.code);
        }
        try {
            message = messaging.createMessage('hello');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-3 createMessage with <<hello>> message type", e.message, e.code);
        }

        try {
            message = messaging.createMessage(123);
        } catch(e) {
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 1-4 createMessage with <<123>> message type", e.message, e.code);
        }

        try {
            message = messaging.createMessage(2);
        } catch(e) {
            assertSame('NOT_SUPPORTED_ERR', 9, e.code);
            console.log("OK 1-5 createMessage with <<2>> message type", e.message, e.code);
        }

        try {
            message = messaging.createMessage(3);
        } catch(e) {
            assertSame('NOT_SUPPORTED_ERR', 9, e.code);
            console.log("OK 1-6 createMessage with <<3>> message type", e.message, e.code);
        }

        message = messaging.createMessage(1);
        assertSame('instanceof Message', true, message instanceof Message);
        console.log("OK 1-7 createMessage successed message type SMS(1)");
    });
};

fsAsync.prototype.test_sendMessage = function(queue){
    var scb, ecb, message;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: createMessage', function(callbacks) {
        message = deviceapis.messaging.createMessage(1);
        message.body = 'hello';
        message.to = ['01086491196','123456','234234234','234234234234'];
    });

    queue.call('Step 1: sendMessage ', function(callbacks) {
        console.log('================== Step 1: sendMessage test ======================================================');
        _APPSPRESSO_REQUEST_URL = '/test/src-test/general_ok.json';
        try {
            deviceapis.messaging.sendMessage(scb,ecb,message);
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-1 sendMessage with scb <<undefined>>", e.message, e.code);
        }

        try {
            deviceapis.messaging.sendMessage(function(){},ecb,'hello');
        } catch(e) {
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log("OK 1-1 sendMessage with message <<hello>>", e.message, e.code);
        }

        var messageSendCallback = {
            onsuccess : callbacks.add(function() {
                console.log('OK 1-2 scb final');
            }),
            onmessagesendsuccess : function(recipient) {
                console.log('OK 1-2 onmessagesendsuccess ', recipient);
            },
            onmessagesenderror : function(e, recipient) {
                console.log('OK 1-3 onmessagesenderror ', recipient, e.message, e.code);
            }
        };

        var ecb = function(e){
            console.log('OK 1-3 ecb final ', e.message, e.code);
        }


        deviceapis.messaging.sendMessage(messageSendCallback,ecb,message);
        setTimeout(function(){
            deviceapis.messaging.callSuccessCallback(30000, true, message.to);
        }, 300);

        deviceapis.messaging.sendMessage(messageSendCallback,ecb,message);
        setTimeout(function(){
            callbacks.add(deviceapis.messaging.callErrorCallback(30001, true, message.to));
        }, 300);


        var messageSendCallback2 = {
            onsuccess : function() {
                console.log('OK 1-4 scb final');
            },
            onmessagesendsuccess : function(recipient) {
                console.log('OK 1-4 onmessagesendsuccess ', recipient);
            },
            onmessagesenderror : callbacks.add(function(e, recipient) {
                console.log('OK 1-4 onmessagesenderror ', recipient, e.message, e.code);
            })
        };

        var ecb2 = callbacks.add(function(e){
            console.log('OK 1-4 ecb final ', e.message, e.code);
            assertSame('UNKNOWN_ERR', 0, e.code);
        });
        deviceapis.messaging.sendMessage(messageSendCallback2,ecb2,message);
        setTimeout(function(){
            deviceapis.messaging.callSuccessCallback(30002, false, ["123","456"]);
        }, 500);
        setTimeout(function(){
            callbacks.add(deviceapis.messaging.callErrorCallback(30002, false, ["456"]));
        }, 500);
        setTimeout(function(){
            callbacks.add(deviceapis.messaging.callSuccessCallback(30002, true, ["123"]))
        }, 500);

    });
};

