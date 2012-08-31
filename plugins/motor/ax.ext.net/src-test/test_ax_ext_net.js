var RESULT_URL = '/test/src-test/result.json';
var ERROR_URL = '/test/src-test/error.json';
_APPSPRESSO_REQUEST_METHOD = 'GET';

TestCase('ax_ext_net', {

    test_ax_ext_net: function() {
        assertTrue(window.hasOwnProperty('ax'));
        assertTrue(ax.hasOwnProperty('ext'));
        assertTrue(ax.ext.hasOwnProperty('net'));
        assertTrue(ax.ext.net.hasOwnProperty('curl'));
        assertTrue(ax.ext.net.hasOwnProperty('get'));
        assertTrue(ax.ext.net.hasOwnProperty('post'));
        assertTrue(ax.ext.net.hasOwnProperty('download'));
        assertTrue(ax.ext.net.hasOwnProperty('upload'));
        assertTrue(ax.ext.net.hasOwnProperty('sendMail'));
    },
    test_curl_no_opts: function() {
        try {
            ax.ext.net.curl();//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_opts: function() {
        try {
            ax.ext.net.curl('***should_be_object***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl(123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_no_url: function() {
        try {
            ax.ext.net.curl({});//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:null});//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_url: function() {
        try {
            ax.ext.net.curl({url:{}});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_method: function() {
        try {
            ax.ext.net.curl({url:'whatever', method:{}});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', method:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_headers: function() {
        try {
            ax.ext.net.curl({url:'whatever', headers:'***should_be_object***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', headers:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_params: function() {
        try {
            ax.ext.net.curl({url:'whatever', params:'***should_be_object***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', params:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_files: function() {
        try {
            ax.ext.net.curl({url:'whatever', files:'***should_be_object***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', files:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_download: function() {
        try {
            ax.ext.net.curl({url:'whatever', download:{}});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', download:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_success: function() {
        try {
            ax.ext.net.curl({url:'whatever', success:'***should_be_function***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', success:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_error: function() {
        try {
            ax.ext.net.curl({url:'whatever', error:'***should_be_function***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', error:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_sent: function() {
        try {
            ax.ext.net.curl({url:'whatever', sent:'***should_be_function***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', sent:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_curl_bad_received: function() {
        try {
            ax.ext.net.curl({url:'whatever', received:'***should_be_function***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.curl({url:'whatever', received:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_get_no_url: function() {
        try {
            ax.ext.net.get();//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_get_bad_url: function() {
        try {
            ax.ext.net.get({});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.get(123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_get_bad_success: function() {
        try {
            ax.ext.net.get('whatever', '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.get('whatever', 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_get_bad_error: function() {
        try {
            ax.ext.net.get('whatever', function(){}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.get('whatever', function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_get_bad_encoding: function() {
        try {
            ax.ext.net.get('whatever', function(){}, function(){}, {});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.get('whatever', function(){}, function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_post_no_url: function() {
        try {
            ax.ext.net.post();//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_post_bad_url: function() {
        try {
            ax.ext.net.post({});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.post(123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_post_bad_params: function() {
        try {
            ax.ext.net.post('whatever', '***should_be_object***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.post('whatever', 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_post_bad_success: function() {
        try {
            ax.ext.net.post('whatever', {}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.post('whatever', {}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_post_bad_error: function() {
        try {
            ax.ext.net.post('whatever', {}, function(){}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.post('whatever', {}, function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_post_bad_encoding: function() {
        try {
            ax.ext.net.post('whatever', {}, function(){}, function(){}, {});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.post('whatever', {}, function(){}, function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_download_no_url: function() {
        try {
            ax.ext.net.download();//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_download_bad_url: function() {
        try {
            ax.ext.net.download({});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.download(123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_download_no_path: function() {
        try {
            ax.ext.net.download('whatever');//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_download_bad_path: function() {
        try {
            ax.ext.net.download('whatever', {});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.download('whatever', 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_download_bad_success: function() {
        try {
            ax.ext.net.download('whatever', 'whatever', '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.download('whatever', 'whatever', 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_download_bad_error: function() {
        try {
            ax.ext.net.download('whatever', 'whatever', function(){}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.download('whatever', 'whatever', function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_download_bad_progress: function() {
        try {
            ax.ext.net.download('whatever', 'whatever', function(){}, function(){}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.download('whatever', 'whatever', function(){}, function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_upload_no_url: function() {
        try {
            ax.ext.net.upload();//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_upload_bad_url: function() {
        try {
            ax.ext.net.upload({});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.upload(123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_upload_bad_params: function() {
        try {
            ax.ext.net.upload('whatever', '***should_be_object***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.upload('whatever', 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_upload_bad_files: function() {
        try {
            ax.ext.net.upload('whatever', {}, '***should_be_object***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.upload('whatever', {}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_upload_bad_success: function() {
        try {
            ax.ext.net.upload('whatever', {}, {}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.upload('whatever', {}, {}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_upload_bad_error: function() {
        try {
            ax.ext.net.upload('whatever', {}, {}, function(){}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.upload('whatever', {}, {}, function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_upload_bad_progress: function() {
        try {
            ax.ext.net.upload('whatever', {}, {}, function(){}, function(){}, '***should_be_function***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.upload('whatever', {}, {}, function(){}, function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_callback: function() {
        try {
            ax.ext.net.sendMail(123, function(){}, {});//***should_be_function***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_errback: function() {
        try {
            ax.ext.net.sendMail(function(){}, 123, {});//***should_be_function***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_no_opts: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){});//***required***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_opts: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){}, '***should_be_object***');
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.sendMail(function(){}, function(){}, 123);
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_subject: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {subject:{}});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {subject:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_message: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {message:{}});//***should_be_string***
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {message:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_to: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {to:'***should_be_array***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {to:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_cc: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {cc:'***should_be_array***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {cc:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_bcc: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {bcc:'***should_be_array***'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {bcc:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    },
    test_sendMail_bad_attachments: function() {
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {attachments:'***should_be_array*'});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
        try {
            ax.ext.net.sendMail(function(){}, function(){}, {attachments:123});
        } catch(err) {
            assertTrue(err instanceof Error);
            assertTrue(ax.isError(err));
            assertEquals(17, err.code);
        }
    }
});
