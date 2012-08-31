var eclipseTesting = AsyncTestCase('eclipseTesting');

eclipseTesting.prototype.test_with_eclipse_external_tool = function(queue) {

    try{

    }catch(e){
        alert(e.message);
    }
};


var fsAsync = AsyncTestCase('fsAsync');

fsAsync.prototype.test_open_stream = function(queue){

    _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    var successCallback, errorCallback, testFile;


    queue.call('Step 1: resolve', function(callbacks) {
        console.log('================== Step 1: resolve & createFile ======================================================');

        successCallback = callbacks.add(function(o) {
            var objFile = o;
            assertObject('File Object', objFile);

            testFile = objFile.createFile('streamNote.txt');
        });

        errorCallback = function(e) {
            console.log('ERR name: ' + e.name);
            console.log('ERR message: ' + e.message);

            assertTrue(e instanceof DeviceAPIError);
        };

        deviceapis.filesystem.resolve(successCallback,
                errorCallback, 'wgt-package', "r");
    });

    queue.call('Step 2: openStream param check', function(callbacks) {
        console.log('================== Step 2: openStream param check ======================================================');

        assertFunction('openStream', testFile.openStream);

        var scb = function(o){};
        var ecb = function(e){};

        try{
            testFile.openStream(scb,ecb);
        }catch(e){
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            console.log('OK Step 2-1: openStream with mode <<undefined>>: ' + e.message + '(' + e.code + ')');
        }

        ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log('OK Step 2-2: openStream with mode rw: ' + e.message + '(' + e.code + ')');
        };
        testFile.openStream(scb,callbacks.add(ecb),'rw');



    });

    queue.call('Step 3: openStream success', function(callbacks) {
        console.log('================== Step 3: openStream success ======================================================');

        assertFunction('openStream', testFile.openStream);

        var scb = function(stream){
            console.log('OK Step 3-1: openStream success: ' + stream);

            assertFunction('write', stream.write);
            assertFunction('writeBytes', stream.writeBytes);
            assertFunction('writeBase64', stream.writeBase64);

            assertFunction('read', stream.read);
            assertFunction('readBytes', stream.readBytes);
            assertFunction('readBase64', stream.readBase64);

            assertFunction('close', stream.close);

            try{
                _APPSPRESSO_REQUEST_URL = '/test/src-test/stream_write_ok.json';
                stream.write('hello');
            }catch(e){
                alert(e.message);
            }

        };
        var ecb = function(e){};

        _APPSPRESSO_REQUEST_URL = '/test/src-test/open_stream_ok.json';
        testFile.openStream(callbacks.add(scb),ecb,'w');

    });

};

fsAsync.prototype.test_copyto_moveto_validate_params = function(queue){
    _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    var scb, ecb, objFile;

    queue.call('Step 1: resolve', function(callbacks) {
        scb = callbacks.add(function(o) {
            objFile = o;
            assertObject('File Object', objFile);
        });
        ecb = function(e) {};

        deviceapis.filesystem.resolve(scb, ecb, 'wgt-package', "r");
    });

    scb = function(o){};
    ecb = function(e){};

    queue.call('Step 2: copyTo validate params', function(callbacks) {
        console.log('copyTo ==================');

        scb = function(){
            fail('this test can not call this scb!');
        };
        ecb = function(e){
            assertSame('NOT_FOUND_ERR', 8, e.code);
            console.log("OK 'documents/index.html', 'documents/streamtest/t.txt'", e.message, e.code);
        };
           objFile.copyTo(scb, callbacks.add(ecb), 'documents/index.html', 'documents/streamtest/t.txt', true);

        ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 'wgt-package/index.html', 'documents/../streamtest/t.txt'", e.message);
        };
           objFile.copyTo(scb, callbacks.add(ecb), 'wgt-package/index.html', 'documents/../streamtest/t.txt', true);

        ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 'wgt-package/../index.html', 'documents/../streamtest/t.txt'", e.message);
        };

           objFile.copyTo(scb, callbacks.add(ecb), 'wgt-package/../index.html', 'documents/../streamtest/t.txt', true);

    });


    queue.call('Step 3: moveTo validate params', function(callbacks) {
        console.log('moveTo ==================');

        scb = function(){
            fail('this test can not call this scb!');
        };
        ecb = function(e){
            assertSame('NOT_FOUND_ERR', 8, e.code);
            console.log("OK 'documents/index.html', 'documents/streamtest/t.txt'", e.message, e.code);
        };
        objFile.moveTo(scb, callbacks.add(ecb), 'documents/index.html', 'documents/streamtest/t.txt', true);

        ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 'wgt-package/index.html', 'documents/../streamtest/t.txt'", e.message);
        };
           objFile.moveTo(scb, callbacks.add(ecb), 'wgt-package/index.html', 'documents/../streamtest/t.txt', true);

        ecb = function(e){
            assertSame('INVALID_VALUES_ERR', 22, e.code);
            console.log("OK 'wgt-package/../index.html', 'documents/../streamtest/t.txt'", e.message);
        };
           objFile.moveTo(scb, callbacks.add(ecb), 'wgt-package/../index.html', 'documents/../streamtest/t.txt', true);

    });

};

fsAsync.prototype.test_validate_callback_arguments = function(queue) {

    queue.call('Step 1: validate callback arguments', function(callbacks) {
           var filesystem = deviceapis.filesystem;
           assertObject('deviceapis.filesystem obj' , filesystem);

           var scb = function(objFile){fail('this test can not call this scb!');};
           var ecb = function(e){console.log('ecb:' + e.message + '(' + e.code + ')');};


        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

           try{
               filesystem.resolve(null, ecb, 'documents', 'rw');
           }catch(e){ console.log('<<null>> successCallback', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(undefined, ecb, 'documents', 'rw');
           }catch(e){ console.log('<<undefined>> successCallback', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve('', ecb, 'documents', 'rw');
           }catch(e){ console.log('<<empty>> successCallback', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, 'hello', 'documents', 'rw');
           }catch(e){ console.log('<<hello>> errorCallback', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }


        var scb = function(objFile){
            assertObject('File Object', objFile);
            console.log('<<null>> errorCallback successed');
        };
        filesystem.resolve(callbacks.add(scb), null, 'documents', 'rw');

           ecb = function(e){
               console.log('location <<../documents>>', e.message); assertSame('INVALID_VALUES_ERR', 22, e.code);
           };
           filesystem.resolve(scb, callbacks.add(ecb), '../documents', 'rw');

           ecb = function(e){
               console.log('location <<aaa../documents>>', e.message); assertSame('INVALID_VALUES_ERR', 22, e.code);
           };
           filesystem.resolve(scb, callbacks.add(ecb), 'aaa../documents', 'rw');

           ecb = function(e){
               console.log('location <<./documents>>', e.message); assertSame('INVALID_VALUES_ERR', 22, e.code);
           };
           filesystem.resolve(scb, callbacks.add(ecb), './documents', 'rw');

           ecb = function(e){
               console.log('location <<documents/..ab>>', e.message); assertSame('INVALID_VALUES_ERR', 22, e.code);
           };
           filesystem.resolve(scb, callbacks.add(ecb), 'documents/..ab', 'rw');
       });
};


fsAsync.prototype.test_read_as_text = function(queue) {

        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        var successCallback, errorCallback, objFile;


         queue.call('Step 1: resolve', function(callbacks) {

            successCallback = callbacks.add(function(o) {
                objFile = o;
                assertObject('File Object', objFile);
            });

            errorCallback = function(e) {};

            deviceapis.filesystem.resolve(successCallback, errorCallback, 'wgt-package', "r");
        });

        queue.call('Step 2: readAsText', function() {

            assertFunction('listFiles', objFile.listFiles);

            _APPSPRESSO_REQUEST_URL = '/test/src-test/read_as_text_ok.json';

            objFile.readAsText(function(str){
                        console.log('Step 2: readAsText :' + str);
                         assertTrue(str,true);

                     }, function(e){
                        assertTrue(e.message,false);
                     }, "UTF-8"
            );


        });

};

fsAsync.prototype.test_create_directory = function(queue) {

    try{
        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        var successCallback, errorCallback, objFile;


         queue.call('Step 1: resolve', function(callbacks) {

            successCallback = callbacks.add(function(o) {
                objFile = o;
                assertObject('File Object', objFile);
            });

            errorCallback = function(e) {};

            deviceapis.filesystem.resolve(successCallback, errorCallback, 'wgt-package', "r");
        });

        queue.call('Step 2: createDirectory', function() {

            assertFunction('listFiles', objFile.listFiles);

            _APPSPRESSO_REQUEST_URL = '/test/src-test/create_directory_ok.json';

            var newDir = objFile.createDirectory('streamtest');
            assertObject('File Object', newDir);
            console.log('Step 2: createDirectory');

            _APPSPRESSO_REQUEST_URL = '/test/src-test/create_directory_failed_already_exist.json';

            try{
                var newDir = objFile.createDirectory('streamtest');
            }catch(e){
                console.log('Step 2-2: createDirectory again with existing dir: ' + e.message + '(' + e.code + ')');
                assertSame('IO_ERROR', 100, e.code);
            }


        });


    }catch(e){
        alert(e.message);
    }
};


fsAsync.prototype.test_resolve_sub = function(queue) {

    try{
        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        var successCallback, errorCallback, objFile;


         queue.call('Step 1: resolve', function(callbacks) {

            successCallback = callbacks.add(function(o) {
                objFile = o;
                console.log('Step 1: objFile: ' + objFile);
                assertObject('File Object', objFile);
            });

            errorCallback = function(e) {
                console.log('ERR name: ' + e.name);
                console.log('ERR message: ' + e.message);

                assertTrue(e instanceof DeviceAPIError);
            };

            deviceapis.filesystem.resolve(successCallback,
                    errorCallback, 'wgt-package', "r");
        });

        queue.call('Step 2: resolve sub', function(callbacks) {

            assertFunction('listFiles', objFile.listFiles);

            _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_sub_ok.json';

            var subObjFile = objFile.resolve('js');

            subObjFile.listFiles(
                callbacks.add(function(files){
                    for(var i = 0; i < files.length; i++)
                    {
                       console.log('Step 2 resolve sub: ' + files[i].fullPath);
                       assertObject('File Object', objFile);
                    }
                }),
                function(e){
                    console.log('Step 2: resolve sub error ' + e.message);
                }
            );
        });

        queue.call('Step 3: resolve sub with invalid filePath', function() {

            assertFunction('listFiles', objFile.listFiles);

            try{
                var subObjFile = objFile.resolve(null);
            }catch(e){
                console.log('Step 3-1: resolve sub with filePath <<null>>', e.message);
                assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            }

            try{
                var subObjFile = objFile.resolve(undefined);
            }catch(e){
                console.log('Step 3-2: resolve sub with filePath <<undefined>>', e.message);
                assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            }


            try{
                var subObjFile = objFile.resolve('');
            }catch(e){
                console.log('Step 3-3: resolve sub with filePath <<empty>>', e.message);
                assertSame('INVALID_VALUES_ERR', 22, e.code);
            }
        });

    }catch(e){
        alert(e.message);
    }
};


fsAsync.prototype.test_create_file = function(queue) {

    try{

        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        var successCallback, errorCallback, objFile;


         queue.call('Step 1: resolve', function(callbacks) {
            console.log('================== Step 1: resolve ======================================================');

            successCallback = callbacks.add(function(o) {
                objFile = o;
                assertObject('File Object', objFile);
            });

            errorCallback = function(e) {
                console.log('ERR name: ' + e.name);
                console.log('ERR message: ' + e.message);

                assertTrue(e instanceof DeviceAPIError);
            };

            deviceapis.filesystem.resolve(successCallback,
                    errorCallback, 'wgt-package', "r");
        });

        queue.call('Step 2: createFile', function() {
            console.log('================== Step 2: createFile with getting attributes ======================================================');

            assertFunction('createFile', objFile.createFile);

            _APPSPRESSO_REQUEST_URL = '/test/src-test/create_file_ok.json';
            _APPSPRESSO_REQUEST_METHOD = 'GET';

            var testFile = objFile.createFile('streamNote.txt');
            console.debug(testFile);
            console.log('Step 2-1: createFile success: ' + testFile.fullPath);


            assertEquals('documents/streamNote.txt', testFile.fullPath);
            assertEquals('documents/', testFile.path);


            assertEquals(true, testFile.isFile);
            assertEquals(false, testFile.isDirectory);


            _APPSPRESSO_REQUEST_URL = '/test/src-test/getFileSize.json';
            assertEquals(0, testFile.fileSize);

            _APPSPRESSO_REQUEST_URL = '/test/src-test/getModified.json';
            console.log('testFile.modified : ' + testFile.modified);
            assertEquals(1321941794000, testFile.modified);

            console.log('testFile.created : ' + testFile.created);
            assertEquals(1321941794000, testFile.created);

            _APPSPRESSO_REQUEST_URL = '/test/src-test/getReadOnly.json';
            console.log('testFile.readOnly : ' + testFile.readOnly);
            assertEquals(false, testFile.readOnly);


            _APPSPRESSO_REQUEST_URL = '/test/src-test/toURI.json';
            console.log('testFile.toURI : ' + testFile.toURI());
            assertEquals('/appspresso/file/documents/streamNote.txt', testFile.toURI());

            console.log('testFile.length : ' + testFile.length);
            assertEquals(undefined, testFile.length)


            _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
            var parent = testFile.parent;
            assertObject('File Object', parent);

            _APPSPRESSO_REQUEST_URL = '/test/src-test/getLength.json';
            console.log('parent.length : ' + parent.length);
            assertEquals(10, parent.length)

            assertFunction('openStream', testFile.openStream);

            try{
                _APPSPRESSO_REQUEST_URL = '/test/src-test/create_file_failed_already_exist.json';
                var testFile = objFile.createFile('streamNote.txt');
            }catch(e){
                console.log('Step 2-3: createFile again with existing file: ' + e.message + '(' + e.code + ')');
                assertSame('IO_ERR', 100, e.code);
            }

        });

        queue.call('Step 3: createFile with invalid path', function() {

            assertFunction('createFile', objFile.createFile);
            try{
                var testFile = objFile.createFile(undefined);

            }catch(e){
                console.log('Step 3-1: createFile with <<undefined>>: ' + e.message + '(' + e.code + ')');
                assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            }


            try{
                var testFile = objFile.createFile(null);
            }catch(e){
                console.log('Step 3-2: createFile with <<null>>: ' + e.message + '(' + e.code + ')');
                assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            }

        });



    }catch(e){
        alert(e.message);
    }
};


fsAsync.prototype.test_listfiles = function(queue) {

    try{
        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';

        var successCallback, errorCallback, objFile;


         queue.call('Step 1: resolve', function(callbacks) {

            successCallback = callbacks.add(function(o) {
                objFile = o;
                console.log('Step 1: objFile: ' + objFile);
                assertObject('File Object', objFile);
            });

            errorCallback = function(e) {
                console.log('ERR name: ' + e.name);
                console.log('ERR message: ' + e.message);

                assertTrue(e instanceof DeviceAPIError);
            };

            deviceapis.filesystem.resolve(successCallback,
                    errorCallback, 'wgt-package', "r");
        });


        queue.call('Step 2: listFiles', function() {

            assertFunction('listFiles', objFile.listFiles);

            var scb2 = function(o) {
                console.log('Step 2: lists: ' + o.length);
                for(var i = 0; i < o.length; i++){
                    assertObject('File Object', o[i]);
                }
            };

            var ecb2 = function(e) {
                console.log(e.message);
            };

            objFile.listFiles(scb2, ecb2);

        });


        queue.call('Step 3: listFiles with Filter', function() {

            assertFunction('listFiles', objFile.listFiles);

            var scb3 = function(o) {
                console.log('Step 3: filtered lists: ' + o.length);
                for(var i = 0; i < o.length; i++){
                    assertObject('File Object', o[i]);
                }
            };

            var ecb3 = function(e) {
                console.log(e.message);
            };
            objFile.listFiles(scb3, ecb3, {'name':'%html'});

        });

        queue.call('Step 4: listFiles with invalid Filter', function() {

            assertFunction('listFiles', objFile.listFiles);
            var scb4 = function(o) {};
            var ecb4 = function(e) {};

            try{
                objFile.listFiles(scb4, ecb4, 'string');
            }catch(e){
                console.log('Step 4: listFiles with invalid Filter', e.message);
                assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            }

        });

        queue.call('Step 4-2: listFiles with invalid Filter', function() {

            assertFunction('listFiles', objFile.listFiles);
            var scb4 = function(o) {};
            var ecb4 = function(e) {};

            try{
                objFile.listFiles(scb4, ecb4, 'Hello!!');
            }catch(e){
                console.log('Step 4-2: listFiles with invalid Filter', e.message);
                assertSame('TYPE_MISMATCH_ERR', 17, e.code);
            }

        });

    }catch(e){
        alert(e.message);
    }
};



TestCase('fsSync', {
    test_filesystem: function() {

        assertTrue(window.hasOwnProperty('FileArray'));
        assertTrue(window.hasOwnProperty('ByteArray'));
        assertTrue(window.hasOwnProperty('FileFilter'));
        assertTrue(window.hasOwnProperty('FileSystemManager'));

        assertTrue(deviceapis.filesystem instanceof FileSystemManager);

   },

   test_resolve_param_mode: function(){

           var filesystem = deviceapis.filesystem;
           assertObject('deviceapis.filesystem obj' , filesystem);

           var scb = function(objFile){console.log('scb objFile: ' + objFile);};
           var ecb = function(e){console.log('ecb: ' + e.message + '(' + e.code + ')');};


        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';


           try{
               filesystem.resolve(scb, ecb, 'documents', 'rw');
               assertTrue(true);
           }catch(e){
               console.log(e.message);
               assertTrue(e.message);
           }


          try{
               filesystem.resolve(scb, ecb, 'documents', null);
           }catch(e){ assertTrue(e.message, false); }

           try{
               filesystem.resolve(scb, ecb, 'documents', undefined);
           }catch(e){ console.log('documents + undefined', e.message); assertTrue(e.message, false); }

           try{
               filesystem.resolve(scb, ecb, 'documents', 123);
           }catch(e){ console.log('documents + 123', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, 'documents', [1,2,3]);
           }catch(e){ console.log('documents + [1,2,3]', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, 'documents', {});
           }catch(e){ console.log('documents + {}', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
            var ecb2 = function(e){
                console.log('ecb2 documents + hello : ' + e.message + '(' + e.code + ')');
                assertSame('INVALID_VALUES_ERR', 22, e.code);
            };
               filesystem.resolve(scb, ecb2, 'documents', 'hello');
           }catch(e){ assertTrue(e.message, false); }

    },
    test_resolve_param_location: function(){

           var filesystem = deviceapis.filesystem;
           assertObject('deviceapis.filesystem obj' , filesystem);

           var scb = function(objFile){console.log('scb objFile: ' + objFile);};
           var ecb = function(e){console.log('ecb: ' + e.message + '(' + e.code + ')');};


        _APPSPRESSO_REQUEST_URL = '/test/src-test/resolve_ok.json';
        _APPSPRESSO_REQUEST_METHOD = 'GET';


           try{
               filesystem.resolve(scb, ecb, undefined, 'rw');
           }catch(e){ console.log('undefined + rw ', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, null, 'rw');
           }catch(e){ console.log('null + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, '', 'rw');
           }catch(e){ console.log('<<empty>> + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, undefined, 'rw');
           }catch(e){ console.log('undefined + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, 0, 'rw');
           }catch(e){ console.log('0 + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, false, 'rw');
           }catch(e){ console.log('false + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, 123, 'rw');
           }catch(e){ console.log('123 + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, [1,2,3], 'rw');
           }catch(e){ console.log('[1,2,3] + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

           try{
               filesystem.resolve(scb, ecb, {}, 'rw');
           }catch(e){ console.log('{} + rw', e.message); assertSame('TYPE_MISMATCH_ERR', 17, e.code); }

   }

});

