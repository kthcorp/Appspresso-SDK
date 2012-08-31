TestCase('ax', {
    test_ax: function() {
        //'window 의 ax 속성으로 접근'
        assertTrue(window.hasOwnProperty('ax'));
    },
    test_runMode: function() {
        //ax.runMode 속성으로 실행 모드를 전환
        assertEquals(ax.MODE_DEBUG, 0);
        assertEquals(ax.MODE_RELEASE, 1);

        assertTrue(ax.hasOwnProperty('runMode'));

        ax.runMode = ax.MODE_DEBUG;
        assertEquals(ax.runMode, ax.MODE_DEBUG);

        ax.runMode = ax.MODE_RELEASE;
        assertEquals(ax.runMode, ax.MODE_RELEASE);
    },
    test_isObject: function() {
        //ax.isObject() 함수로 객체 여부를 검사
        assertTrue(ax.hasOwnProperty('isObject'));
        assertTrue(typeof ax.isObject === 'function');

        assertFalse(ax.isObject(true));
        assertFalse(ax.isObject(false));

        assertFalse(ax.isObject('string'));//NOTE!!!
        assertTrue(ax.isObject(new String('string')));//NOTE!!!

        assertTrue(ax.isObject(null));
        assertTrue(ax.isObject({}));
        assertTrue(ax.isObject([]));
        assertTrue(ax.isObject(new Object()));
        assertTrue(ax.isObject(new Array()));
    },
    test_isFunction: function() {
        //ax.isFunction() 함수로 함수 여부를 검사
        assertTrue(ax.isFunction(function() {}));
        assertTrue(ax.isFunction(new Function()));
        assertTrue(ax.isFunction(document.getElementById));
    },
    test_isString: function() {
        //ax.isString() 함수로 문자열 여부를 검사
        assertFalse(ax.isString(undefined));
        assertFalse(ax.isString(true));
        assertFalse(ax.isString(false))
        assertFalse(ax.isString(null));
        assertFalse(ax.isString({}));
        assertFalse(ax.isString([]));
        assertTrue(ax.isString(''));
        assertTrue(ax.isString('string'));
        assertTrue(ax.isString(new String('string')));//NOTE!!!
    },
    test_isNumber: function() {
        //ax.isNumber() 함수로 숫자 여부를 검사
        assertFalse(ax.isNumber(undefined));
        assertFalse(ax.isNumber(null));
        assertFalse(ax.isNumber(Number.NaN));
        assertFalse(ax.isNumber('string'));
        assertFalse(ax.isNumber('123'));
        assertFalse(ax.isNumber(true));
        assertFalse(ax.isNumber(false));
        assertFalse(ax.isNumber([]));
        assertFalse(ax.isNumber({}));
        assertFalse(ax.isNumber(function(){}));
        assertTrue(ax.isNumber(0));
        assertTrue(ax.isNumber(Number.MIN_VALUE));
        assertTrue(ax.isNumber(Number.MAX_VALUE));
        assertTrue(ax.isNumber(Number.NEGATIVE_INFINITY));
        assertTrue(ax.isNumber(Number.POSITIVE_INFINITY));
    },
    test_isBoolean: function() {
        //ax.isNumber() 함수로 숫자 여부를 검사
        assertFalse(ax.isBoolean(undefined));
        assertFalse(ax.isBoolean(null));
        assertFalse(ax.isBoolean(Number.NaN));
        assertFalse(ax.isBoolean('string'));
        assertFalse(ax.isBoolean('123'));
        assertFalse(ax.isBoolean([]));
        assertFalse(ax.isBoolean({}));
        assertFalse(ax.isBoolean(function(){}));
        assertTrue(ax.isBoolean(true));
        assertTrue(ax.isBoolean(false));
    },
    test_isArray: function() {
        //ax.isArray() 함수로 배열 여부를 검사
        assertTrue(ax.isArray([]));
        assertTrue(ax.isArray(new Array()));

        assertFalse(ax.isArray({}));
    },
    test_isArrayLike: function() {
        //ax.isArrayLike() 함수로 유사배열 여부를 검사
        assertTrue(ax.isArrayLike([]));
        assertTrue(ax.isArrayLike(arguments));
        assertTrue(ax.isArrayLike(document.childNodes));
    },
    test_def: function() {
        //ax.def().end()로 객체 생성
        var obj = ax.def().end();
        assertTrue(ax.isObject(obj));

        var obj2 = ax.def(obj).end();
        assertTrue(ax.isObject(obj2));
        assertEquals(obj2, obj);
    },
    test_def_constant: function() {
        //ax.def().constant().end()로 객체에 상수 추가
        var obj = ax.def().constant('constant', 123).end();

        assertTrue(obj.hasOwnProperty('constant'));
        assertEquals(123, obj.constant);

        try {
            obj.constant = 456;
            fail();
        } catch(e) {
            assertEquals(123, obj.constant);
        }
    },
    test_def_property: function() {
        //ax.def().property().end()로 객체에 속성 추가
        var propertyValue = 123;
        function getter() { return propertyValue; }
        function setter(newValue) { propertyValue = newValue; }

        var obj = ax.def().property('property', getter, setter).end();

        assertTrue(obj.hasOwnProperty('property'));
        assertEquals(123, obj.property);

        try {
            obj.property = 456;
            assertEquals(456, obj.property);
            assertEquals(propertyValue, obj.property);
        } catch(e) {
            fail();
        }
    },
    test_def_method: function() {
        //ax.def().method().end()로 객체에 메소드 추가
        function func(a, b) { return a + b; }

        var obj = ax.def().method('method', func).end();

        assertTrue(obj.hasOwnProperty('method'));
        assertTrue(ax.isFunction(obj.method));
        assertEquals(func, obj.method);
        assertEquals(30, obj.method(10, 20));

        try {
            obj.method = null;
            fail();
        } catch(e2) {
            assertTrue(ax.isFunction(obj.method));
            assertEquals(func, obj.method);
        }
        assertEquals(30, obj.method(10, 20));
    },
    test_def_ns: function() {
        var obj1 = { a:1, b:2 };
        var obj2 = ax.def(obj1).ns('one.two.three').end();
        assertEquals(obj1, obj2);
        assertEquals(obj1, one.two.three);
        var obj3 = ax.def(obj1).ns('a.b.c', one).end();
        assertEquals(obj1, obj3);
        assertEquals(obj1, one.a.b.c);
    },
    test_def_mixin: function() {
        //두 개의 객체를 병합
        var obj1 = { a:1, b:2 };
        var obj2 = { b:22, c:33 };
        var obj3 = ax.def(obj1).mixin(obj2).end();

        assertEquals(obj3.a, 1);
        assertEquals(obj3.b, 22);
        assertEquals(obj3.c, 33);
    },
    test_def_mixin_null: function() {
        //객체와 null/undefined을 병합
        var obj1 = { a:1, b:2 };

        assertEquals(obj1, ax.def(obj1).mixin().end());
        assertEquals(obj1, ax.def(obj1).mixin(null).end());
        assertEquals(obj1, ax.def().mixin(obj1).end());
        assertEquals(obj1, ax.def(null).mixin(obj1).end());
    }
});