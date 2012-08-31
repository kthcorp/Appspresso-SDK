var FOUND_URL = '/test/src-test/result.json';
var NOTFOUND_URL = '/__not_found__';
TestCase('ax_util', {
    test_ax_util: function() {
        //ax 의 util 속성으로 접근
        assertTrue(ax.hasOwnProperty('util'));
    },

    test_evaluateJavaScript: function() {
        //자바스크립트 문자열을 (안전하게) 파싱
        assertTrue(ax.util.evaluateJavaScript('true'));
        assertFalse(ax.util.evaluateJavaScript('false'));
        assertEquals(ax.util.evaluateJavaScript('1'), 1);
        assertEquals(ax.util.evaluateJavaScript('"string"'), 'string');
    },

    test_decodeJSON: function() {
        //JSON 문자열을 (안전하게) 파싱
        var json = '{"string":"string", "number":123, "boolean":true, "array":[1,2,3], "object":{"a":1, "b":2}}';
        var obj = ax.util.decodeJSON(json);
        assertTrue(typeof obj.string === 'string');
        assertTrue(typeof obj.number === 'number');
        assertTrue(typeof obj.boolean === 'boolean');
        assertTrue(typeof obj.array === 'object' && obj.array instanceof Array);
        assertTrue(typeof obj.object === 'object');
    },

    test_decodeJSON_empty: function() {
        //undefined/null/empty를 (조용히) 빈 객체({})로 파싱
        assertEquals(ax.util.decodeJSON(), {});
        assertEquals(ax.util.decodeJSON(null), {});
        assertEquals(ax.util.decodeJSON(''), {});
    },

    test_encodeJSON: function() {
        //자바스크립트 객체를 JSON문자열로 변환
        var obj = {
            "string" : "string",
            "number" : 123,
            "boolean" : true,
            "array" : [ 1, 2, 3 ],
            "object" : {
                "a" : 1,
                "b" : 2
            }
        };
        var json = ax.util.encodeJSON(obj);
        assertTrue(typeof json === 'string');
    },

    test_encodeJSON_empty: function() {
        //undefined/null/empty도 (조용히) 변환
        assertEquals(ax.util.decodeJSON(undefined), {});
        assertEquals(ax.util.decodeJSON(null), {});
        assertEquals(ax.util.decodeJSON(''), {});
    },

    test_foreach_object: function() {
        //객체의 속성들을 순회
        var obj = {
            "string" : "string",
            "number" : 123,
            "boolean" : true
        };
        var ret = {};
        ax.util.foreach(obj, function(k, v) {
            ret[k] = v;
        });
        assertEquals(ret, obj);
    },

    test_foreach_array: function() {
        //배열의 항목들을 순회
        var arr = [ "string", 123, true ];
        var ret = {};
        ax.util.foreach(arr, function(k, v) {
            ret[k] = v;
        });
        assertEquals(ret, arr);
    },

    test_foreach_arraylike: function() {
        //유사배열의 항목/속성들을 순회
        var arrlike = document.childNodes;
        var ret = [];
        var i = 0;
        ax.util.foreach(arrlike, function(k, v) {
            console.log(k, '=', v);
            ret[i++] = v;
        });
        assertEquals(ret.length, arrlike.length);
    },

    test_dump: function() {
        //자바스크립트 객체를 (사람이 볼 수 있는) 문자열로 변환
        var obj = {
            "string" : "string",
            "number" : 123,
            "boolean" : true,
            "array" : [ 1, 2, 3 ],
            "object" : {
                "a" : 1,
                "b" : 2
            },
            "array2" : [
                [
                    [ 1,2,3 ]
                ]
            ],
            "object2" : {
                "object3" : {
                    "object4" : {
                        "a" : 1,
                        "b" : 2
                    }
                }
            },
            'undefined': undefined,
            'null': null,
            "function": function() {
            },
            "arguments": arguments,
            "nodelist": document.getElementsByTagName('script')
        };
        var ret = ax.util.dump(obj);
        console.log('ax.util.dump:', obj, '==>', ret);
        assertTrue(typeof ret === 'string');
    },
    test_format: function() {
        //문자열 내의 마커를 치환
        var ret = ax.util.format('{0} {1} {2}', 'one', 'two', 'three');
        assertEquals('one two three', ret);
    },
    test_format_order: function() {
        //순서가 바뀌어도 됨
        var ret = ax.util.format('{2} {1} {0}', 'one', 'two', 'three');
        assertEquals('three two one', ret);
    },
    test_format_noargs: function() {
        //인자가 없으면 문자열 그대로
        var ret = ax.util.format('this is test');
        assertEquals('this is test', ret);
    },
    test_format_bad_args: function() {
        //마커에 부합하는 인자가 없으면 마커를 그대로 남겨둠
        var ret = ax.util.format('{0} {1} {2}', 'one', 'two');
        assertEquals('one two {2}', ret);
    },
    test_getFunctionName_named: function() {
        //이름있는 함수 지원
        function named(){}
        assertEquals('named', ax.util.getFunctionName(named));
    },
    test_getFunctionName_inline: function() {
        //이름있는 인라인 함수 지원
        assertEquals('named_inline', ax.util.getFunctionName(function named_inline(){}));
    },
    test_getFunctionName_anonymous: function() {
        //익명함수 지원
        assertEquals('<<anonymous-function>>', ax.util.getFunctionName(function(){}));
    },
    test_getFunctionName_not_func: function() {
        //함수가 아니라도 (조용히) 문자열을 리턴
        assertEquals('<<invalid-function>>', ax.util.getFunctionName(null));
        assertEquals('<<invalid-function>>', ax.util.getFunctionName({}));
        assertEquals('<<invalid-function>>', ax.util.getFunctionName([]));
        assertEquals('<<invalid-function>>', ax.util.getFunctionName('string'));
        assertEquals('<<invalid-function>>', ax.util.getFunctionName(0));
        assertEquals('<<invalid-function>>', ax.util.getFunctionName(1));
        assertEquals('<<invalid-function>>', ax.util.getFunctionName(true));
        assertEquals('<<invalid-function>>', ax.util.getFunctionName(false));
    }
});

AsyncTestCase('ax_util_async', {
    test_invokeLater: function(q) {
        var state = false;

        function willBeInvokedLater() {
            jstestdriver.console.log('invoked later...');
            state = true;
        }

        q.defer(function(pool) {
            ax.util.invokeLater(null, pool.add(willBeInvokedLater));
            assertFalse(state);
        });
        q.defer(function() {
            assertTrue(state);
        });
    },
    test_ajax_get_async_ok: function(q) {
        //성공하면 onload 호출
        var state = false;
        q.defer(function(pool) {
            var xhr = ax.util.ajax({
                method: 'GET',
                url: FOUND_URL,
                async: true,
                onload: pool.add(function() { state = true; }),
                onerror: pool.addErrback(function() {})
            });
            assertTrue(xhr instanceof XMLHttpRequest);
        });
        q.defer(function() {
            assertTrue(state);
        });
    },
    test_ajax_get_async_error: function(q) {
        //실패하면 onerror 호출
        var state = false;
        q.defer(function(pool) {
            var xhr = ax.util.ajax({
                method: 'GET',
                url: NOTFOUND_URL,
                async: true,
                onload: pool.addErrback(function() {}),
                onerror: pool.add(function() { state = true; })
            });
            assertTrue(xhr instanceof XMLHttpRequest);
        });
        q.defer(function() {
            assertTrue(state);
        });
    }
});

