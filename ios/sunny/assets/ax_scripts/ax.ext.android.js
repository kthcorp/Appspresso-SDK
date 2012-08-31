/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Android Extension API
 * @version 1.0
 */
(function(g) {
    "use strict";
    var _DEBUG = !!g._APPSPRESSO_DEBUG;
    //------------------------------------------------

    var PLUGIN_ID = 'ax.ext.android';
    var PLUGIN_NS = 'ax.ext.android';

    /**
     * Android Extension API - 안드로이드 전용 디바이스 확장 API
     *
     * @namespace
     * @name ax.ext.android
     */

    /**
     * finish this app.
     * 앱 종료.
     *
     * @param {number} exitCode (optional; default:0)
     * @methodOf ax.ext.android
     *
     * @example
     * ax.ext.android.finish();
     */
    function finish(exitCode) {
        this.execAsync('finish', ax.nop, ax.nop, [ exitCode || 0 ]);
    }

    var _backPressed = null;

    /**
     * native callback for back pressed.
     *
     * @methodOf ax.ext.android
     * @private
     */
    function onBackPressed() {
        if (typeof _backPressed === 'function') {
            _backPressed();
        }
    }

    /**
     * set callback on back pressed.
     * 백 버튼 터치시 콜백 호출.
     *
     * @param {function} callback null to clear callback - 콜백을 제거하려면 null 지정
     * @methodOf ax.ext.android
     *
     * @example
     * var callback = function(e){
     *        var current = location.href;
     *        current = current.substring(current.indexOf('index.html'), current.length);
     *
     *        if(current == 'index.html'){
     *            var message = 'Quit?';
     *            ax.ext.ui.confirm(function(e){
     *                if(e == true){
     *                    ax.ext.android.finish();
     *                }
     *            }, message, {});
     *
     *        }else{
     *            history.go(-1);
     *        }
     * }
     *
     * ax.ext.android.setOnBackPressed(callback);
     */
    function setOnBackPressed(callback) {
        _backPressed = callback;
        this.execAsync('setOnBackPressed', ax.nop, ax.nop, [ !!callback ]);
    }

    var _optionsItemSelected = null;

    /**
     * native callback for options item selected.
     *
     * @param itemId
     * @methodOf ax.ext.android
     * @private
     */
    function onOptionsItemSelected(itemId) {
        if (typeof _optionsItemSelected === 'function') {
            _optionsItemSelected(itemId);
        }
    }

    /**
     * set callback for options item selected.
     * 옵션 화면에서 메뉴 선택 시 콜백 호출.
     *
     * @param {function} callback null to clear callback - 콜백을 제거하려면 null 지정
     * @methodOf ax.ext.android
     *
     * @example
     * // clear callback
     * ax.ext.android.setOnOptionsItemSelected(null);
     */
    function setOnOptionsItemSelected(callback) {
        _optionsItemSelected = callback;
        this.execAsync('setOnOptionsItemSelected', ax.nop, ax.nop, [ !!callback ]);
    }

    /**
     * set options items.
     * 옵션 버튼 터치시 보여질 메뉴 설정.
     * <a href="#.setOnOptionsItemSelected">setOnOptionsItemSelected</a>과 함께 사용.
     *
     * @param {array<string>} items
     * @methodOf ax.ext.android
     *
     * @example
     * var callback = function(selectedMenu){
     *        if (selectedMenu === 0){
     *            alert('Hello!');
     *        } else if (selectedMenu === 1){
     *            ax.ext.android.finish();
     *        }
     * }
     * ax.ext.android.setOnOptionsItemSelected(callback);
     * ax.ext.android.setOptionsItems(['say hello','Exit']);
     */
    function setOptionsItems(items) {
        this.execAsync('setOptionsItems', ax.nop, ax.nop, [ items || [] ]);
    }

    //------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'finish': finish,
        'setOnBackPressed': setOnBackPressed,
        'setOnOptionsItemSelected': setOnOptionsItemSelected,
        'setOptionsItems': setOptionsItems,
        // events
        'onBackPressed': onBackPressed,
        'onOptionsItemSelected': onOptionsItemSelected
    }, PLUGIN_NS);
}(window));
