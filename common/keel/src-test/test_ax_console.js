TestCase('ax_console', {
    test_ax_console: function() {
        //ax.console으로 접근할 수 있어야 함
        assertTrue(ax.hasOwnProperty('console'));
    },
    test_level_all: function() {
        ax.console.level = ax.console.LEVEL_ALL;
        assertEquals(ax.console.LEVEL_ALL, ax.console.level);
        assertTrue(ax.console.traceEnabled);
        assertTrue(ax.console.debugEnabled);
        assertTrue(ax.console.infoEnabled);
        assertTrue(ax.console.warnEnabled);
        assertTrue(ax.console.errorEnabled);

        ax.console.trace('trace!');
        ax.console.debug('debug!');
        ax.console.info('info!');
        ax.console.warn('warn!');
        ax.console.error('error!');
    },
    test_level_trace: function() {
        ax.console.level = ax.console.LEVEL_TRACE;
        assertEquals(ax.console.LEVEL_TRACE, ax.console.level);
        assertTrue(ax.console.traceEnabled);
        assertTrue(ax.console.debugEnabled);
        assertTrue(ax.console.infoEnabled);
        assertTrue(ax.console.warnEnabled);
        assertTrue(ax.console.errorEnabled);

        ax.console.trace('trace!');
        ax.console.debug('debug!');
        ax.console.info('info!');
        ax.console.warn('warn!');
        ax.console.error('error!');
    },
    test_level_debug: function() {
        ax.console.level = ax.console.LEVEL_DEBUG;
        assertEquals(ax.console.LEVEL_DEBUG, ax.console.level);
        assertFalse(ax.console.traceEnabled);
        assertTrue(ax.console.debugEnabled);
        assertTrue(ax.console.infoEnabled);
        assertTrue(ax.console.warnEnabled);
        assertTrue(ax.console.errorEnabled);

        ax.console.trace('trace! (not visible)');
        ax.console.debug('debug!');
        ax.console.info('info!');
        ax.console.warn('warn!');
        ax.console.error('error!');
    },
    test_level_info: function() {
        ax.console.level = ax.console.LEVEL_INFO;
        assertEquals(ax.console.LEVEL_INFO, ax.console.level);
        assertFalse(ax.console.traceEnabled);
        assertFalse(ax.console.debugEnabled);
        assertTrue(ax.console.infoEnabled);
        assertTrue(ax.console.warnEnabled);
        assertTrue(ax.console.errorEnabled);

        ax.console.trace('trace! (not visible)');
        ax.console.debug('debug! (not visible)');
        ax.console.info('info!');
        ax.console.warn('warn!');
        ax.console.error('error!');
    },
    test_level_warn: function() {
        ax.console.level = ax.console.LEVEL_WARN;
        assertEquals(ax.console.LEVEL_WARN, ax.console.level);
        assertFalse(ax.console.traceEnabled);
        assertFalse(ax.console.debugEnabled);
        assertFalse(ax.console.infoEnabled);
        assertTrue(ax.console.warnEnabled);
        assertTrue(ax.console.errorEnabled);

        ax.console.trace('trace! (not visible)');
        ax.console.debug('debug! (not visible)');
        ax.console.info('info! (not visible)');
        ax.console.warn('warn!');
        ax.console.error('error!');
    },
    test_level_error: function() {
        ax.console.level = ax.console.LEVEL_ERROR;
        assertEquals(ax.console.LEVEL_ERROR, ax.console.level);
        assertFalse(ax.console.traceEnabled);
        assertFalse(ax.console.debugEnabled);
        assertFalse(ax.console.infoEnabled);
        assertFalse(ax.console.warnEnabled);
        assertTrue(ax.console.errorEnabled);

        ax.console.trace('trace! (not visible)');
        ax.console.debug('debug! (not visible)');
        ax.console.info('info! (not visible)');
        ax.console.warn('warn! (not visible)');
        ax.console.error('error!');
    },
    test_level_none: function() {
        ax.console.level = ax.console.LEVEL_NONE;
        assertEquals(ax.console.LEVEL_NONE, ax.console.level);
        assertFalse(ax.console.traceEnabled);
        assertFalse(ax.console.debugEnabled);
        assertFalse(ax.console.infoEnabled);
        assertFalse(ax.console.warnEnabled);
        assertFalse(ax.console.errorEnabled);

        ax.console.trace('trace! (not visible)');
        ax.console.debug('debug! (not visible)');
        ax.console.info('info! (not visible)');
        ax.console.warn('warn! (not visible)');
        ax.console.error('error! (not visible)');
    },
    test_redirect: function() {
        var x = console;

        jstestdriver.console.log('before startRedirect...');
        console.dir('dir!');
        console.log('log!');
        console.trace('trace!');
        console.debug('debug!');
        console.info('info!');
        console.warn('warn!');
        console.error('error!');

        ax.console.startRedirect();
        assertNotEquals(x, console);

        jstestdriver.console.log('after startRedirect...');
        console.dir('dir!');
        console.log('log!');
        console.trace('trace!');
        console.debug('debug!');
        console.info('info!');
        console.warn('warn!');
        console.error('error!');

        ax.console.stopRedirect();
        assertEquals(x, console);

        jstestdriver.console.log('after stopRedirect...');
        console.dir('dir!');
        console.log('log!');
        console.trace('trace!');
        console.debug('debug!');
        console.info('info!');
        console.warn('warn!');
        console.error('error!');
    }
});