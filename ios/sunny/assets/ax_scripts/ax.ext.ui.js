/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview UI Extension API
 * @version 1.0
 */
(function(g) {
    "use strict";
    var _DEBUG = !!g._APPSPRESSO_DEBUG;
    //-------------------------------------------------------------

    var PLUGIN_ID = 'ax.ext.ui';
    var PLUGIN_NS = 'ax.ext.ui';

    /**
     * UI Extension API.
     *
     * @namespace
     * @name ax.ext.ui
     */

    /**
     *
     * alert, pick, confirm, prompt 매소드 옵션 파라미터로 사용될 객체
     *
     * @class
     * @name UiOpts
     * @property {string} title 타이틀
     * @property {string} positive 확인 버튼 텍스트
     * @property {string} negative 취소 버튼 텍스트
     * @property {string} placeholder default 텍스트 (prompt 에서 사용)
     * @property {string} cancel pick에서 사용 되는 Cancel 버튼 텍스트 (ios only)
     * @property {string} destructive pick에서 사용 되는 부정적인(붉은색) 버튼 텍스트 (ios only)
     * @memberOf ax.ext.ui
     * @see ax.ext.ui.alert
     * @see ax.ext.ui.pick
     * @see ax.ext.ui.confirm
     * @see ax.ext.ui.prompt
     */

    /**
     * show alert dialog.
     *
     * @param {function} callback
     * @param {string} message
     * @param {ax.ext.ui.UiOpts} opts (optional)
     * @return AxRequest
     * @methodOf ax.ext.ui
     *
     * @example
     * var scb = function(){
     *     console.log('Positive Button touched');
     * };
     *
     * //var opts = {'title':'Title'};
     * //var opts = {'positive':'Positive Button'};
     * var opts = {'positive':'Positive Button', 'title':'Title'};
     * ax.ext.ui.alert(scb, 'Alert Message!', opts);
     *
     * // no option
     * // ax.ext.ui.alert(scb, 'Alert Message!');
     */
    function alert(callback, message, opts) {
        return this.execAsync('alert', callback, ax.nop, [ message || '', opts || {} ]);
    }

    /**
     * show confirm dialog.
     *
     * @param {function(boolean)} callback
     * @param {string} message
     * @param {ax.ext.ui.UiOpts} opts
     * @return AxRequest
     * @methodOf ax.ext.ui
     *
     * @example
     * var scb = function(res){
     *     if(res === true){
     *         console.log('OK selected');
     *     }else{
     *         console.log('Cancel selected');
     *     }
     * };
     *
     * //var opts = {'title':'Title'};
     * //var opts = {'positive':'Positive Button'};
     * //var opts = {'negative':'Negative Button'};
     * //var opts = {'positive':'Positive Button', 'negative':'Negative Button'};
     * var opts = {'positive':'Positive Button', 'negative':'Negative Button', 'title':'Title'};
     *
     * ax.ext.ui.confirm(scb, 'Confirm Message!', opts);
     *
     * // no option
     * // ax.ext.ui.confirm(scb, 'Confirm Message!');
     */
    function confirm(callback, message, opts) {
        return this.execAsync('confirm', callback, ax.nop, [ message || '', opts || {} ]);
    }

    /**
     * show prompt dialog.
     *
     * @param {function(string)} callback
     * @param {string} message
     * @param {string} value
     * @param {ax.ext.ui.UiOpts} opts (optional)
     * @return AxRequest
     * @methodOf ax.ext.ui
     *
     * @example
     * var scb = function(res){
     *     console.log('user input text: ' + res);
     * };
     *
     * var opts = {'title':'Title'}; // recommended
     * //var opts = {'positive':'Positive Button'};
     * //var opts = {'negative':'Negative Button'};
     * //var opts = {'positive':'Positive Button', 'negative':'Negative Button'};
     * //var opts = {'positive':'Positive Button', 'negative':'Negative Button', 'title':'Title'};
     *
     * ax.ext.ui.prompt(scb, 'Prompt Message!','default text',opts);
     *
     * // placeholder with options - only for iOS
     * // var opts = {'title':'Title', 'placeholder':'placeholder with options'};
     * // ax.ext.ui.prompt(scb, 'Prompt Message!', null, opts);
     *
     * // no option
     * // ax.ext.ui.prompt(scb, 'Prompt Message!','placeholder');
     */
    function prompt(callback, message, value, opts) {
        return this.execAsync('prompt', callback, ax.nop, [ message || '', value || '', opts || {} ]);
    }

    /**
     * show item picker with native look and feel.
     *
     * @param {function(number)} callback
     * @param {array.&lt;string&gt;} items
     * @param {ax.ext.ui.UiOpts} opts
     * @return AxRequest
     * @methodOf ax.ext.ui
     *
     * @example
     * var scb = function(pickedButtonIdx){
     *     console.log('picked button idx: ' + pickedButtonIdx);
     * };
     *
     * var arrayItems = ['item 1', 'item 2'];
     *
     * var opts = {'title':'Title'};
     * //var opts = {'cancel':'Cancel Button'}; // only for iOS
     * //var opts = {'destructive':'Destructive Button'}; // only for iOS
     * //var opts = {'cancel':'Cancel Button', 'destructive':'Destructive Button'}; // only for iOS
     *
     * ax.ext.ui.pick(scb, arrayItems, opts);
     *
     * // no option
     * // ax.ext.ui.pick(scb, arrayItems);
     */
    function pick(callback, items, opts) {
        return this.execAsync('pick', callback, ax.nop, [ items || [], opts || {} ]);
    }

    /**
     * open a url with platform native manner.
     * 단말의 기본 브라우저로 입력된 웹 페이지를 엽니다.
     *
     * @param {string} url
     * @param {object.&lt;string,*&gt;} opts (optinal)
     * @methodOf ax.ext.ui
     *
     * @example
     * ax.ext.ui.open('http://appspresso.com/');
     */
    function open(url, opts) {
        return this.execAsync('open', ax.nop, ax.nop, [ url, opts || {} ]);
    }

    /**
     * show progress indicator with native look and feel.
     *
     * @param {string} message
     * @param {object.&lt;string,*&gt;} opts (optional)
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.hideProgress
     * @example
     * ax.ext.ui.showProgress('Progress Message!');
     */
    function showProgress(message, opts) {
        return this.execSync('showProgress', [ message || '', opts || {} ]);
    }

    /**
     * hide progress indicator.
     *
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.showProgress
     * @example
     * ax.ext.ui.hideProgress();
     */
    function hideProgress() {
        return this.execSync('hideProgress', [ ]);
    }

    /**
     * show status bar on top of screen.
     * 딘말의 상태바를 보입니다.
     *
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.hideStatusBar
     * @example
     * ax.ext.ui.showStatusBar();
     */
    function showStatusBar() {
        return this.execAsync('showStatusBar', ax.nop, ax.nop, [ ]);
    }

    /**
     * hide status bar on top of screen.
     * 단말의 상태바를 감춥니다.
     *
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.showStatusBar
     * @example
     * ax.ext.ui.hideStatusBar();
     */
    function hideStatusBar() {
        return this.execAsync('hideStatusBar', ax.nop, ax.nop, [ ]);
    }

    var startCallback = null;

    function onStart(url, type) {
        if (typeof startCallback === 'function') {
            return startCallback(url, type);
        }
        return true;
    }

    var finishCallback = null;

    function onFinish(url, type) {
        if (typeof finishCallback === 'function') {
            return finishCallback(url, type);
        }
        return true;
    }

    var errorCallback = null;

    function onError(url, type) {
        if (typeof errorCallback === 'function') {
            return errorCallback(url, type);
        }
        return true;
    }

    var loadCallback = null;

    function onLoad(url, type) {
        if (typeof loadCallback === 'function') {
            return loadCallback(url, type);
        }
        return true;
    }

    /**
     * addWebView 옵션 파라미터로 사용될 객체
     *
     * @class
     * @name AddWebViewOpts
     * @property {number} top
     * @property {number} left
     * @property {number} width
     * @property {number} height
     * @memberOf ax.ext.ui
     * @see ax.ext.ui.addWebView
     */

    /**
     * WebView 주소 이동 load 이벤트 콜백
     *
     * @function
     * @memberOf ax.ext.ui.AddWebViewOpts#
     * @param {string} handle
     * @param {string} url
     * @param {number} type (optional)
     * @name load
     *
     * @example
     * var opts = {
     *        load: function(handle, url, type){
     *            console.log(handle, url, type);
     *        }
     *    };
     * var url = 'http://appspresso.com';
     *
     * var handle = ax.ext.ui.addWebView(url, opts);
     */

    /**
     * WebView 주소 이동 start 이벤트 콜백
     *
     * @function
     * @memberOf ax.ext.ui.AddWebViewOpts#
     * @param {string} handle
     * @param {string} url
     * @name start
     *
     * @example
     * var opts = {
     *        start: function(handle, url){
     *            console.log(handle, url);
     *        }
     *    };
     * var url = 'http://appspresso.com';
     *
     * var handle = ax.ext.ui.addWebView(url, opts);
     */


    /**
     * WebView 주소 이동 finish 이벤트 콜백
     *
     * @function
     * @memberOf ax.ext.ui.AddWebViewOpts#
     * @param {string} handle
     * @param {string} url
     * @name finish
     *
     * @example
     * var opts = {
     *        finish: function(handle, url){
     *            console.log(handle, url);
     *        }
     *    };
     * var url = 'http://appspresso.com';
     *
     * var handle = ax.ext.ui.addWebView(url, opts);
     */

    /**
     * WebView 주소 이동 error 이벤트 콜백
     *
     * @function
     * @memberOf ax.ext.ui.AddWebViewOpts#
     * @param {string} handle
     * @param {string} url
     * @param {string} errorMessage (optional)
     * @param {number} errorCode (optional)
     * @name error
     *
     * @example
     * var opts = {
     *        error: function(handle, url, errorMessage, errorCode){
     *            console.log(handle, url, errorMessage, errorCode);
     *        }
     *    };
     * var url = 'http://appspresso.com';
     *
     * var handle = ax.ext.ui.addWebView(url, opts);
     */




    /**
     * add an webview on this webview(aka. child browser).
     *
     * @param {function(string)} callback
     * @param {string} url
     * @param {ax.ext.ui.AddWebViewOpts} opts
     * @methodOf ax.ext.ui
     * @return {string} handle
     * @see ax.ext.ui.removeWebView
     * @example
     * var loadCallback = function(handle, url){
     *         if(url.indexOf('http://HOOKING_URL_FOR_REMOVING_WEBVIEW') != 0){
     *            return;
     *         }
     *         ax.ext.ui.removeWebView(function(){},handle);
     * };
     * var callback = function(){
     *      console.log('webview added');
     * }
     *
     * var opts = {
     *         'top': 43,
     *         'left': 0,
     *         'width': 320,
     *         'height': 417,
     *         'load': loadCallback
     * };
     * var url = 'http://appspresso.com/';
     *
     * var handle = ax.ext.ui.addWebView(callbback, url, opts);
     * console.log('handle:', handle);
     */
    function addWebView(callback, url, opts) {

        if (opts) {
            if (typeof opts.start === 'function') {
                startCallback = opts.start;
                opts.start = 1;
            } else {
                opts.start = -1;
            }
            if (typeof opts.finish === 'function') {
                finishCallback = opts.finish;
                opts.finish = 1;
            } else {
                opts.finish = -1;
            }
            if (typeof opts.error === 'function') {
                errorCallback = opts.error;
                opts.error = 1;
            } else {
                opts.error = -1;
            }
            if (typeof opts.load === 'function') {
                loadCallback = opts.load;
                opts.load = 1;
            } else {
                opts.load = -1;
            }
        }

        return this.execAsync('addWebView', callback || ax.nop, ax.nop, [ url, opts || {} ]);
    }

    /**
     * remove an web view was created with addWebView.
     *
     * @param {function} callback
     * @param {string} handle
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.addWebView
     *
     * @example
     * ...
     * var handle = ax.ext.ui.addWebView(url);
     * ...
     *
     * var scb = function(){
     *     console.log('webview closed');
     * };
     * ax.ext.ui.removeWebView(scb, handle);
     */
    function removeWebView(callback, handle) {
        return this.execAsync('removeWebView', callback || ax.nop, ax.nop, [ handle ]);
    }

    /**
     * @class
     * @name AddTextViewOpts
     * @property {number} top
     * @property {number} left
     * @property {number} width
     * @property {number} height
     * @property {number} maxLength
     * @memberOf ax.ext.ui
     * @see ax.ext.ui.addTextView
     *
     * @example
     * var opts = {
     *     'height': 100,
     *     'left': 20,
     *     'maxLength': 100,
     *     'top': 50,
     *     'width': 300
     * };
     */

    /**
     * add a native text view(multiline editable text field).
     *
     * @param {function(handle)} callback
     * @param {string} text
     * @param {ax.ext.ui.AddTextViewOpts} opts
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.removeTextView
     *
     * @example
     * var scb = function(){
     *     console.log('text view added');
     * };
     * var opts = {
     *     'height': 100,
     *     'left': 20,
     *     'maxLength': 100,
     *     'top': 50,
     *     'width': 300
     * };
     *
     * ax.ext.ui.addTextView(scb, 'default text', opts);
     */
    function addTextView(callback, text, opts) {
        return this.execAsync('addTextView', callback || ax.nop, ax.nop, [ text || '', opts || {} ]);
    }

    /**
     * remove a text view was created with addTextView.
     *
     * @param {function(string)} callback
     * @param {string} handle
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.addTextView
     *
     * @example
     * var scb = function(txt){
     *     console.log(txt);
     * };
     *
     * ax.ext.ui.removeTextView(scb);
     */
    function removeTextView(callback, handle) {
        return this.execAsync('removeTextView', callback || ax.nop, ax.nop, [ handle ]);
    }

    /**
     * set application's orientation and use custom setting.
     *
     * @param {integer} orientation
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.setOrientation
     *
     * ax.ext.ui.setOrientation(1);
     */
    function setOrientation(orientation) {
        return this.execAsync('setOrientation', ax.nop, ax.nop, [ orientation ]);
    }

    /**
     * get application's orientation. Before setting orientation, returns real orientation.
     *
     * @param {function(string)} callback
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.getOrientation
     *
     * @example
     * var scb = function(txt){
     *     console.log(txt);
     * };
     *
     * ax.ext.ui.getOrientation(scb);
     */
    function getOrientation(callback) {
        return this.execAsync('getOrientation', callback || ax.nop, ax.nop, [ ]);
    }

    /**
     * reset custom orientation setting and back to project orientation setting
     *
     * @methodOf ax.ext.ui
     * @see ax.ext.ui.resetOrientation
     *
     * ax.ext.ui.resetOrientation();
     */
    function resetOrientation() {
        return this.execAsync('resetOrientation', ax.nop, ax.nop, [ ]);
    }


    //-------------------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'alert': alert,
        'confirm': confirm,
        'prompt': prompt,
        'pick': pick,
        'open': open,
        'showProgress': showProgress,
        'hideProgress': hideProgress,
        'showStatusBar': showStatusBar,
        'hideStatusBar': hideStatusBar,
        'setOrientation' : setOrientation,
        'getOrientation' : getOrientation,
        'resetOrientation' : resetOrientation,
        //XXX:undocumented!
        'addWebView': addWebView,
        'removeWebView': removeWebView,
        'addTextView': addTextView,
        'removeTextView': removeTextView,
        //events
        'onStart': onStart,
        'onFinish': onFinish,
        'onError': onError,
        'onLoad': onLoad
    }, PLUGIN_NS);
}(window));
