TestCase('ax_cbstore', {

    test_cbstore: function() {
        assertTrue(ax.hasOwnProperty('cbstore'));
    },

    test_register: function() {
        var st = ax.cbstore,
            fn = function() {};
        st.register(42, fn, fn);

        var p = st.peek(42);
        assertObject(p);
        assertFunction(p.cb);
        assertFunction(p.eb);
        assertSame(p.cb, fn);
        assertSame(p.eb, fn);

        st.clear(42);
    },

    test_clear: function() {
        var st = ax.cbstore,
            fn = function() {};

        assertUndefined(st.peek(42));

        st.register(42, fn, fn);
        assertNotUndefined(st.peek(42));

        st.clear(42);
        assertUndefined(st.peek(42));
    },

    test_pop: function() {
        var st = ax.cbstore,
            fn = function() {};

        assertUndefined(st.peek(42));

        st.register(42, fn, fn);
        assertNotUndefined(st.peek(42));

        var p = st.pop(42);
        assertUndefined(st.peek(42));

        assertObject(p);
        assertFunction(p.cb);
        assertFunction(p.eb);
        assertSame(p.cb, fn);
        assertSame(p.eb, fn);
    }

});
