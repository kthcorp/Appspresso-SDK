TestCase('ax_error', {
    test_error_code_message_cause: function() {
        // 읽기 전용 속성 code, message, cause을 가짐
        var cause = new Error('cause');
        var err = ax.error(1, 'error', cause);
        console.log(err);
        assertTrue(ax.isError(err));
        assertEquals(1, err.code);
        assertEquals('error', err.message);
        assertEquals(cause, err.cause);
    },
    test_ax_error_code_message: function() {
        var err = ax.error(1, 'error');
        console.log(err);
        assertTrue(ax.isError(err));
        assertEquals(1, err.code);
        assertEquals('error', err.message);
        assertEquals(null, err.cause);
    },
    test_ax_error_message_default: function() {
        var err = ax.error(17);
        console.log(err);
        assertTrue(ax.isError(err));
        assertEquals(17, err.code);
        assertEquals('TYPE_MISMATCH_ERR', err.message);
        assertEquals(null, err.cause);
    },
    test_ax_error_code: function() {
        var err = ax.error(-1);
        console.log(err);
        assertTrue(ax.isError(err));
        assertEquals(-1, err.code);
        assertEquals('UNKNOWN_ERR', err.message);
        assertEquals(null, err.cause);
    },
    test_ax_error: function() {
        var err = ax.error();
        console.log(err);
        assertTrue(ax.isError(err));
        assertEquals(0, err.code);
        assertEquals('UNKNOWN_ERR', err.message);
        assertEquals(null, err.cause);
    }
});
