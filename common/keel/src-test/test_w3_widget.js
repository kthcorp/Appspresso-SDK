_APPSPRESSO_CONFIG_XML_URL = '/test/src-test/config.xml';

TestCase('w3_widget', {
    test_w3_widget: function() {
        assertTrue(window.hasOwnProperty('widget'));
    },

    test_attrs: function() {
        var wgt = window.widget;
        assertNotNull(wgt);

        // wgt.preference 객체의 프로퍼티를 조회하려면 sail에 jsonrpc call 해야하므로 dump 불가능..
        //jstestdriver.console.log(ax.util.dump(wgt));

        assertEquals('author', wgt.author);
        assertEquals('description', wgt.description);
        assertEquals('name', wgt.name);
        assertEquals('shortName', wgt.shortName);
        assertEquals('version', wgt.version);
        assertEquals('id', wgt.id);
        assertEquals('authorEmail', wgt.authorEmail);
        assertEquals('authorHref', wgt.authorHref);
        assertNotNull(wgt.preferences);
        assertEquals(320, wgt.width);
        assertEquals(480, wgt.height);
    }

});
