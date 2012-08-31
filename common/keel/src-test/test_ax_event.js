TestCase('ax_event', {

    test_event: function() {
        assertTrue(ax.hasOwnProperty('event'));
    },

    test_simple: function() {
        // 이벤트 핸들러 등록하고 호출
        var t1 = false;

        function f1() { t1 = true; }

        ax.event.on('simple', f1);
        ax.event.trigger('simple');

        assertTrue(t1);

        ax.event.off('simple');
    },

    test_param: function() {
        // 이벤트 핸들러 호출할 때 파라미터 전달
        var t1 = false;

        function f1(p1, p2) {
            if (p1 === 'bar' && p2 === 'baz') {
                t1 = true;
            }
        }

        ax.event.on('param', f1);
        ax.event.trigger('param', ['bar', 'baz']);

        assertTrue(t1);

        ax.event.off('param');
    },

    test_multi: function() {
        // 이벤트 핸들러 여러개 등록하고 호출
        var t1 = 0;

        function f1() { t1++; }
        function f2() { t1++; }

        ax.event.on('multi', f1);
        ax.event.on('multi', f2);
        ax.event.trigger('multi');

        assertEquals(t1, 2);

        ax.event.off('multi');
    },

    test_off: function() {
        // 이벤트 핸들러 제거
        var t1 = 0;

        function f1() { t1++; }
        function f2() { t1++; }

        ax.event.on('off', f1);
        ax.event.on('off', f2);
        ax.event.trigger('off');
        assertEquals(t1, 2);

        ax.event.off('off', f1);
        ax.event.trigger('off');
        assertEquals(t1, 3);

        ax.event.off('off');
        ax.event.trigger('off');
        assertEquals(t1, 3);
    },

    test_one: function() {
        // 한번만 실행되는 이벤트 핸들러
        var t1 = 0;

        function f1() { t1++; }
        function f2() { t1++; }
        function f3() { t1++; }

        ax.event.on('one', f1);
        ax.event.one('one', f2);
        ax.event.one('one', f3);

        ax.event.trigger('one');
        assertEquals(t1, 3);

        ax.event.trigger('one');
        assertEquals(t1, 4);

        ax.event.off('one');
    },

    test_viabridge: function() {
        // 브릿지를 통한 이벤트 처리
        var t1 = 0;

        ax.event.one('viabridge', function f1() { t1++; });

        ax.bridge.jsonrpc(JSON.stringify({
            id: null,
            method: 'ax.event.trigger',
            params: [{ type: 'viabridge' }]
        }));

        assertEquals(t1, 1);
    }

});

AsyncTestCase('ax_event_async', {

    test_ready: function(q) {
        // 특정 상태를 기다리는 핸들러 설치와 호출
        var t1 = 0;

        function f1(val) { t1 = val; }

        ax.event.ready('foo', f1);

        q.defer(function() {
            ax.event.done('foo', [42]);
        });

        q.defer(function() {
            assertEquals(t1, 42);
        });

        q.defer(function() {
            ax.event.done('foo', [43]);
        });

        q.defer(function() {
            assertNotEquals(t1, 43);
        });
    },

    test_already: function(q) {
        // 이미 특정 상태가 되었을 때, 핸들러 설치하면 호출되는지 테스트
        var t1 = 0;

        function f1(val) { t1 += val; }
        function f2(val) { t1 += val; }

        ax.event.done('foo', [42]);

        q.defer(function() {
            ax.event.ready('foo', f1);
        });

        q.defer(function() {
            assertEquals(t1, 42);
        });

        q.defer(function() {
            ax.event.ready('foo', f2);
        });

        q.defer(function() {
            assertEquals(t1, 84);
        });
    }

});
