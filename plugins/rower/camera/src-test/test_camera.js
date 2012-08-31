var eclipseTesting = AsyncTestCase('eclipseTesting');

eclipseTesting.prototype.test_with_eclipse_external_tool = function(queue) {

};

var fsAsync = AsyncTestCase('fsAsync');

fsAsync.prototype.test_captureImage = function(queue){
    var scb, ecb, mainCamera;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: getCameras && createPreviewNode', function(callbacks) {
        scb = callbacks.add(function(cams){
            mainCamera = cams[0];
            mainCamera.createPreviewNode(function(preview){
                    document.getElementsByTagName("body")[0].appendChild(preview);
                    preview.style.visibility = "visible";
                }, ecb);
        });
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getCameras_ok.json';
        deviceapis.camera.getCameras(scb, ecb);
    });

    queue.call('Step 1: captureImage', function(callbacks) {
        console.log('================== Step 1: captureImage ======================================================');

        try{
            mainCamera.captureImage(undefined, ecb);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-1 captureImage with invalid scb <<undefined>>:", e.message, e.code); };

        try{
            mainCamera.captureImage(null, ecb);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-2 captureImage with invalid scb <<null>>:", e.message, e.code); };

        scb = function(filename){};
        ecb = function(e){
            console.log("FAIL 1-3 captureImage FAILED (known problem...): " + e.message, e.code);
        };

        //TODO: 무조건 INVALID_STATE_ERR(11) 오류 떨어짐. window jstestdriver 가 여러 윈도우 객체를 가지고 있어 정상적으로 동작안하는것으로 보임... - althjs
        //XXX: checkPreview 에서 진행이 안되므로 테스트불가 ㅠㅠ 임시로 테스트 시에는 checkPreview를 주석처리 하도록... - althjs
        try{
            mainCamera.captureImage(scb, ecb, 'hello');
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-3 captureImage with invalid options <<hello>>:", e.message, e.code); };

        try{
            mainCamera.captureImage(scb, ecb, 123);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-4 captureImage with invalid options <<123>>:", e.message, e.code); };

        try{
            mainCamera.captureImage(scb, ecb, {'highRes':'YES'});
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-5 highRes option with <<YES>>:", e.message, e.code); };

        try{
            mainCamera.captureImage(scb, ecb, {'highRes':1});
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-6 highRes option with <<1>>:", e.message, e.code); };

        var ecb1 = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 1-7 destinationFilename option with <<..>> " + e.message, e.code);
        };
        mainCamera.captureImage(scb, ecb1, {'destinationFilename':'..'});

        var ecb2 = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 1-8 destinationFilename option with <<documents/../abcd.jpg>> " + e.message, e.code);
        };
        mainCamera.captureImage(scb, ecb2, {'destinationFilename':'documents/../abcd.jpg'});


        var op = null;
        scb = function(filename){
            assertSame('captured filename', 'videos/a.mp4', filename);
            console.log("OK 1-9 Captured file path:" +  filename);
            op = null;
        };

        ecb = function(e){
            console.log("FAIL 1-9 captureImage FAILED (known problem...): " + e.message, e.code);
        };

        _APPSPRESSO_REQUEST_URL = '/test/src-test/capture_ok.json';
        op = mainCamera.captureImage(scb, ecb);

    });
};

fsAsync.prototype.test_startVideoCapture = function(queue){

    var scb, ecb, mainCamera;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: getCameras && createPreviewNode', function(callbacks) {
        scb = callbacks.add(function(cams){
            mainCamera = cams[0];
            mainCamera.createPreviewNode(function(preview){
                    document.getElementsByTagName("body")[0].appendChild(preview);
                    preview.style.visibility = "visible";
                }, ecb);
        });
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getCameras_ok.json';
        deviceapis.camera.getCameras(scb, ecb);
    });

    queue.call('Step 1: startVideoCapture', function(callbacks) {
        console.log('================== Step 1: startVideoCapture ======================================================');

        try{
            mainCamera.startVideoCapture(undefined, ecb);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-1 startVideoCapture with invalid scb <<undefined>>:", e.message, e.code); };

        try{
            mainCamera.startVideoCapture(null, ecb);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-2 startVideoCapture with invalid scb <<null>>:", e.message, e.code); };

        scb = function(filename){};
        ecb = function(e){
            console.log("FAIL 1-3 captureImage FAILED (known problem...): " + e.message, e.code);
        };

        //TODO: 무조건 INVALID_STATE_ERR(11) 오류 떨어짐. window jstestdriver 가 여러 윈도우 객체를 가지고 있어 정상적으로 동작안하는것으로 보임... - althjs
        //XXX: checkPreview 에서 진행이 안되므로 테스트불가 ㅠㅠ 임시로 테스트 시에는 checkPreview를 주석처리 하도록... - althjs
        try{
            mainCamera.startVideoCapture(scb, ecb, 'hello');
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-3 startVideoCapture with invalid options <<hello>>:", e.message, e.code); };

        try{
            mainCamera.startVideoCapture(scb, ecb, 123);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-4 startVideoCapture with invalid options <<123>>:", e.message, e.code); };

        try{
            mainCamera.startVideoCapture(scb, ecb, {'highRes':'YES'});
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-5 startVideoCapture option with <<YES>>:", e.message, e.code); };

        try{
            mainCamera.startVideoCapture(scb, ecb, {'highRes':1});
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-6 startVideoCapture option with <<1>>:", e.message, e.code); };

        var ecb1 = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 1-7 destinationFilename option with <<..>> " + e.message, e.code);
        };
        mainCamera.startVideoCapture(scb, ecb1, {'destinationFilename':'..'});

        var ecb2 = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 1-8 destinationFilename option with <<documents/../abcd.jpg>> " + e.message, e.code);
        };
        mainCamera.startVideoCapture(scb, ecb2, {'destinationFilename':'documents/../abcd.jpg'});


        scb = function(filename){
            assertSame('captured filename', 'videos/a.mp4', filename);
            alert("OK 1-9 Captured file path:" +  filename);
        };
        ecb = function(e){
            console.log("FAIL 1-9 startVideoCapture FAILED (known problem...): " + e.message, e.code);
        };
        //TODO: !monitorInfo[this.id].vdoCapturing 에서  false가 떨어져 진행이 안됨... - althjs
        _APPSPRESSO_REQUEST_URL = '/test/src-test/capture_ok.json';
        mainCamera.startVideoCapture(scb, ecb);

        setTimeout(function(){
                mainCamera.stopVideoCapture();
            }, 2000);

    });
};

fsAsync.prototype.test_createPreviewNode = function(queue){
    var scb, ecb, mainCamera;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: getCameras', function(callbacks) {
        scb = callbacks.add(function(cams){
            mainCamera = cams[0];
        });
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getCameras_ok.json';
        deviceapis.camera.getCameras(scb, ecb);
    });

    queue.call('Step 1: createPreviewNode', function(callbacks) {
        console.log('================== Step 1: createPreviewNode ======================================================');

        try{
            mainCamera.createPreviewNode(undefined, ecb);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-1 createPreviewNode with invalid scb <<undefined>>:", e.message, e.code); };

        try{
            mainCamera.createPreviewNode(null, ecb);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-2 createPreviewNode with invalid scb <<null>>:", e.message, e.code); };

        scb = function(preview){
            //preview.style.visibility = "visible";

            var bd = document.getElementsByTagName("body")[0];
            bd.appendChild(preview);

            assertSame('preview dom element', '<div id="_AppspressoCameraPreviewNode_Camera"></div>', bd.innerHTML);
            console.log('OK 1-3 createPreviewNode successed: ' + bd.innerHTML);


            ecb = function(e){
                assertSame('INVALID_STATE_ERR', 11, e.code);
                console.log('OK 1-4 createPreviewNode again:', e.message,e.code);
            };

            mainCamera.createPreviewNode(scb, ecb);
        };

        mainCamera.createPreviewNode(scb, ecb);

    });
};

fsAsync.prototype.test_getCameras = function(queue){
    var scb, ecb, mainCamera;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 1: getCameras', function(callbacks) {
        console.log('================== Step 1: getAddressBooks ======================================================');

        var camera = deviceapis.camera;
        assertObject('deviceapis.camera obj', camera);

        try {
            camera.getCameras(scb, ecb);
        } catch(e) {
            console.log("OK getCameras with <<undefined>> scb", e.message, e.code);
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
        }

        scb = function(cams){
            mainCamera = cams[0];

            assertFunction('captureImage', mainCamera.captureImage);
            assertFunction('startVideoCapture', mainCamera.startVideoCapture);
            assertFunction('stopVideoCapture', mainCamera.stopVideoCapture);
            assertFunction('createPreviewNode', mainCamera.createPreviewNode);

            console.log("OK getCameras scb");
        };
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getCameras_ok.json';
        camera.getCameras(scb, ecb);
    });
};


