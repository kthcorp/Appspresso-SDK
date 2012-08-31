/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
/**
 * @fileOverview 네트웍 관련 확장 API
 * @version 1.0
 */
(function(g) {
    "use strict";
    //-------------------------------------------------------------

    var _DEBUG = !!g._APPSPRESSO_DEBUG,
        PLUGIN_ID = 'ax.ext.net',
        PLUGIN_NS = 'ax.ext.net',
        DEF_METHOD = 'GET',
        DEF_ENCODING = 'UTF-8',
        DEF_TIMEOUT = 0; // 0 for no timeout

    /**
     * 네트웍 관련 확장 API.
     *
     * @namespace
     * @name ax.ext.net
     */

    /**
     * curl/get/post/upload/download 등의 호출이 성공했을 때 결과로 CurlCallback의 파라메터로 전달되는 HTTP 응답 정보가 담긴 객체.
     *
     * @class
     * @name CurlResult
     * @property {number} status 응답 상태 코드(예: 200이면 성공, 404는 NOT FOUND로 실패 등...)
     * @property {string} data 응답 본문 데이터 문자열(download 호출의 결과일 경우 이 값은 유효하지 않음)
     * @property {object.&lt;string,string&gt;} headers 응답 헤더
     * @memberOf ax.ext.net
     * @see ax.ext.net.CurlSuccessCallback
     */

    /**
     * curl/get/post/upload/donwload 등의 호출이 성공했을 때 호출될 콜백 함수.
     *
     * @class
     * @name CurlSuccessCallback
     * @param {ax.ext.net.CurlResult} HTTP 응답 정보가 담긴 객체
     * @memberOf ax.ext.net
     * @see ax.ext.net.curl
     *
     * @example
     * var successCB = function(o){    // ax.ext.net.CurlSuccessCallback
     *     console.log('data:', o.data);
     *     console.log('status:', o.status);
     *
     *     for(var i in o.headers){
     *           console.log(i, o.headers[i]);
     *        }
     * };
     */

    /**
     * curl/get/post/upload/donwload 등의 호출이 실패했을 때 호출될 콜백 함수.
     *
     * @class
     * @name CurlErrorCallback
     * @param {AxError} error 에러 객체
     * @memberOf ax.ext.net
     * @see ax.ext.net.curl
     *
     * @example
     * var errorCB = function(e){
     *     console.log('errorCB:', e.message, e.code);
     * };
     */

    /**
     * curl/upload 등에 의한 HTTP 요청의 "송신" 진행 상황에 따라 수시로 호출될 콜백 함수.
     *
     * @class
     * @name CurlSentCallback
     * @param {Array.&lt;number&gt;} progress 송신한 바이트 수, 전체 바이트 수가 담긴 길이 2인 배열
     * @memberOf ax.ext.net
     * @see ax.ext.net.curl
     * @see ax.ext.net.upload
     *
     * @example
     * var progressCB = function(progress){    // ax.ext.net.CurlSent
     *     console.log('successCB:', progress[0], progress[1]);
     * };
     */

    /**
     * curl/donwload 등에 의한 HTTP 요청의 "수신" 진행 상황에 따라 수시로 호출될 콜백 함수.
     *
     * @class
     * @name CurlReceivedCallback
     * @param {Array.&lt;number&gt;} progress 수신한 바이트 수, 전체 바이트 수가 담긴 길이 2인 배열
     * @memberOf ax.ext.net
     * @see ax.ext.net.curl
     * @see ax.ext.net.download
     *
     * @example
     * var progressCB = function(progress){    // ax.ext.net.CurlReceived
     *     console.log('successCB:', progress[0], progress[1]);
     * };
     */

    /**
     * curl 함수 호출에 필요한 HTTP 요청 정보가 담긴 객체.
     *
     * @class
     * @name CurlOpts
     * @memberOf ax.ext.net
     * @property {string}
     *      url 요청 URL(필수)
     * @property {string} method
     *      요청 메소드(선택; 기본값:GET)
     * @property {object.&lt;string,string&gt;} headers
     *      요청 헤더(선택)
     * @property {object.&lt;string,string&gt;} params
     *      요청 파라메터(선택).
     *      이 값이 지정되면 application/x-www-form-urlencode 형식으로 POST 요청을 보냄.
     * @property {object.&lt;string,string&gt;} files
     *      파일 업로드 요청 파라메터(선택).
     *      이 값이 지정되면 multipart/form-data 형식으로 POST 요청을 보냄.
     *      파일 경로는 deviceapis.filesystem API와 동일한 가상 절대 경로로 지정.
     * @property {string} download
     *      응답 본문을 저장할 파일 경로(선택).
     *      이 값이 지정되면 {ax.ext.net.CurlResult}의 data 속성은 유효하지 않음.
     * @property {string} encoding
     *      요청 인코딩(선택; 기본값:UTF-8). 지원하는 인코딩을 플랫폼에 따라 다름.
     *      서버로 부터의 응답에 인코딩 정보가 없을 경우에도 사용됨.
     * @property {number} timeout
     *      요청 제한시간(선택; 기본값:0).
     *      지정한 시간내에 요청이 성공하지 않으면 실패한 것으로 간주하고 에러 콜백을 호출함.
     *      지정하지 않으면(또는 0이하의 값을 지정하면) 웹브라우져와 웹서버의 기본 제한시간을 사용함.
     * @property {ax.ext.net.CurlSuccessCallback} success
     *      요청이 성공했을때 호출될 콜백 함수(선택)
     * @property {ax.ext.net.CurlErrorCallback} error
     *      요청이 실패했을 때 호출될 콜백 함수(선택)
     * @property {ax.ext.net.CurlReceivedCallback} progress
     *      요청 수신 진행 상황에 따라 주기적으로 호출될 콜백 함수(선택)
     * @property {ax.ext.net.CurlSentCallback} sent
     *      요청 송신 진행 상황에 따라 주기적으로 호출될 콜백 함수(선택)
     *
     * @see ax.ext.net.curl
     *
     * @example
     * var successCB = function(o){
     *     console.log('successCB: ' + o.data);
     * };
     *
     * var progressCB = function(progress){
     *     console.log('progressCB: ' + progress[0] + progress[1]);
     * };
     *
     * var errorCB = function(e){
     *     console.log('errorCB:', e.message, e.code);
     * };
     *
     * //var files = { 'file1':'downloads/abcd.jpg'};
     * var files = {},
     *     url = 'http://appspresso.com/api/curl.html';
     *
     * var options = {
     *         'url': url,
     *         'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
     *         'params': {'param1':'param1 value', 'param2': 'param2 value', 'param3': 'param3 value'},
     *         'files': files,
     *         'success': successCB,
     *         'error': errorCB,
     *         'sent': progressCB,
     *         //'encoding': 'euc-kr',
     *         'method':'POST'
     *         };
     *
     * ax.ext.net.curl(options);
     */

    /**
     * curl/get/post/upload/donwload 등의 호출이 성공했을 때 호출될 콜백 함수.
     *
     * @function
     * @memberOf ax.ext.net.CurlOpts#
     * @param {ax.ext.net.CurlResult} HTTP 응답 정보가 담긴 객체
     * @name success
     * @see ax.ext.net.CurlSuccessCallback
     */

    /**
     * curl/get/post/upload/donwload 등의 호출이 실패했을 때 호출될 콜백 함수.
     *
     * @function
     * @memberOf ax.ext.net.CurlOpts#
     * @param {AxError} error 에러 객체
     * @name error
     * @see ax.ext.net.CurlErrback
     */

    /**
     * curl/upload/donwload 등에 의한 HTTP 요청의 "수신" 진행 상황에 따라 수시로 호출될 콜백 함수.
     *
     * @function
     * @memberOf ax.ext.net.CurlOpts#
     * @param {Array.&lt;number&gt;} progress 수신한 바이트 수, 전체 바이트 수가 담긴 길이 2인 배열
     * @name sent
     * @see ax.ext.net.CurlReceived
     */

    /**
     * URL을 기반으로하는 HTTP 요청을 수행.
     * <p>
     * XMLHttpRequest와 유사하지만, 크로스 도메인 제약이 없고,
     * upload/download 등의 바이너리 데이터 송수신을 지원함.
     *
     * @param {ax.ext.net.CurlOpts} opts
     * @methodOf ax.ext.net
     */
    function curl(opts) {
        // validate params
        if (!ax.isObject(opts)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (!ax.isString(opts.url)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.method && !ax.isString(opts.method)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.headers && !ax.isObject(opts.headers)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.params && !ax.isObject(opts.params)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.files && !ax.isObject(opts.files)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.download && !ax.isString(opts.download)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.encoding && !ax.isString(opts.encoding)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.timeout && !ax.isNumber(opts.timeout)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.success && !ax.isFunction(opts.success)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.error && !ax.isFunction(opts.error)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.sent && !ax.isFunction(opts.sent)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.received && !ax.isFunction(opts.received)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }

        var params = {
            url: opts.url,
            method: opts.method || DEF_METHOD,
            headers: opts.headers || {},
            params: opts.params || {},
            files: opts.files || {},
            download: opts.download || '',
            encoding: opts.encoding || DEF_ENCODING,
            sent: !!opts.sent,
            received: !!opts.received
        }, self = this;

        var clearUp = function() {
            if (watchID !== null) {
                self.stopWatch('__removeContext', watchID);
            }
            if (timeout !== null) {
                g.clearTimeout(timeout);
            }
            timeout = watchID = null;

        }, callbackWrapper = function(result) {
            var kind = result.kind,
                payload = result.payload;

            //validation
            if (kind !== 'success' && kind !== 'sent' && kind !== 'received') {
                if (_DEBUG) {
                    ax.debug('malformed net.curl success callback from runtime: {0} - {1}', kind, ax.util.encodeJSON(payload));
                }
                return;
            }
            opts[kind] && opts[kind](payload);

            if (kind === 'success') {
                clearUp();
            }

        }, errorbackWrapper = function(error) {
            clearUp();
            opts.error && opts.error(error);
        };

        var watchID = this.watch('curl', callbackWrapper, errorbackWrapper, [params]),
            timeout = null;

        // XXX: 현재는 웹에서 타임아웃 처리, 제대로 하려면 네이티브에서 해야...
        if (opts.timeout > 0) {
            timeout = g.setTimeout(function() {
                try {
                    opts.error && opts.error(ax.error(ax.TIMEOUT_ERR, 'ax.ext.net.curl request timeout'));
                } catch (e) {
                    if (_DEBUG) {
                        ax.debug('uncaught exception from function {0}: {1}',
                            ax.util.getFunctionName(ecb),
                            ax.util.encodeJSON(e));
                    }
                }
                timeout = null;
                clearUp();

            }, opts.timeout);
        }
    }

    /**
     * HTTP "GET" 요청을 수행.
     * <p>
     * 내부적으로는 {ax.ext.net.curl} 함수를 호출함.
     *
     * @param {string} url
     *      url 요청 URL(필수)
     * @param {ax.ext.net.CurlSuccessCallback} success
     *      요청이 성공했을때 호출될 콜백 함수(선택)
     * @param {ax.ext.net.CurlErrorCallback} error
     *      요청이 실패했을 때 호출될 콜백 함수(선택)
     * @param {string} encoding
     *      요청 인코딩(선택; 기본값:UTF-8).
     * @methodOf ax.ext.net
     *
     * @example
     * var scb = function(o){
     *     console.log('scb called', o.data);
     * };
     * var ecb = function(e){
     *     console.log('ecb: ', e.message, e.code);
     * };
     *
     * var url = 'http://appspresso.com/feed/rss';
     * ax.ext.net.get(url, scb, ecb);
     */
    function get(url, success, error, encoding) {
        // validate params
        if (!ax.isString(url)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (success && !ax.isFunction(success)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (error && !ax.isFunction(error)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (encoding && !ax.isString(encoding)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }

        var opts = {
            'method':'GET',
            'url':url,
            'headers':{},
            'params':{},
            'files':{},
            'success':success,
            'error':error,
            'encoding':encoding
        };
        return this.curl(opts);
    }

    /**
     * HTTP "POST" 요청을 수행.
     * <p>
     * 내부적으로는 {ax.ext.net.curl} 함수를 호출함.
     *
     * @param {string} url
     *      url 요청 URL(필수)
     * @param {object.&lt;string,string&gt;} params
     *      요청 파라메터(선택).
     * @param {ax.ext.net.CurlSuccessCallback} success
     *      요청이 성공했을때 호출될 콜백 함수(선택)
     * @param {ax.ext.net.CurlErrorCallback} error
     *      요청이 실패했을 때 호출될 콜백 함수(선택)
     * @param {string} encoding (optional; default:UTF-8)
     *      요청 인코딩(선택; 기본값:UTF-8).
     * @methodOf ax.ext.net
     *
     * @example
     * var params = {'param1': 'param1 value', 'param2': 'param2 value'};
     * var scb = function(o){
     *     console.log('scb called', o.data);
     * };
     * var ecb = function(e){
     *     console.log('ecb: ', e.message, e.code);
     * };
     *
     * var url = 'http://appspresso.com/feed/rss';
     * ax.ext.net.post(url, params, scb, ecb, 'UTF-8');
     */
    function post(url, params, success, error, encoding) {
        // validate params
        if (!ax.isString(url)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (params && !ax.isObject(params)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (success && !ax.isFunction(success)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (error && !ax.isFunction(error)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (encoding && !ax.isString(encoding)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }

        var opts = {
            'method':'POST',
            'url':url,
            'headers':{},
            'params':params || {},
            'files':{},
            'success':success,
            'error':error,
            'encoding':encoding || 'UTF-8'
        };
        return this.curl(opts);
    }

    /**
     * HTTP "GET" 요청을 수행하여 파일을 다운로드.
     * <p>
     * 내부적으로는 {ax.ext.net.curl} 함수를 호출함.
     *
     * @param {string} url
     *      url 요청 URL(필수)
     * @param {string} path
     *      응답 본문을 저장할 파일 경로(필수).
     * @param {ax.ext.net.CurlSuccessCallback} success
     *      요청이 성공했을 때 호출될 콜백 함수(선택)
     * @param {ax.ext.net.CurlErrorCallback} error
     *      요청이 실패했을 때 호출될 콜백 함수(선택)
     * @param {ax.ext.net.CurlReceivedCallback} progress
     *      요청 수신 진행 상황에 따라 주기적으로 호출될 콜백 함수(선택)
     * @methodOf ax.ext.net
     *
     * @example
     * function fileDownloadDemo(){
     *
     *     var url = 'http://appspresso.com/api/icon.png',
     *         path = 'downloads/icon.png';
     *
     *     var successCB = function(o){
     *         console.log('successCB called');
     *
     *         deviceapis.filesystem.resolve(
     *             function(file){
     *                 document.body.innerHTML+= '<img src="' + file.toURI() + '" />';
     *             }, function(e){
     *                 console.log('resolve err:' + e.message);
     *             }, path,  "r"
     *         );
     *
     *     };
     *     var progressCB = function(progress){    // ax.ext.net.CurlReceived
     *         console.log('progressCB:', progress[0], progress[1]);
     *     };
     *     var errorCB = function(e){
     *         console.log('errorCB:', e.message, e.code);
     *     };
     *
     *     ax.ext.net.download(url, path, successCB, errorCB, progressCB);
     *
     * }
     */
    function download(url, path, success, error, progress) {
        // validate params
        if (!ax.isString(url)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (!ax.isString(path)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (success && !ax.isFunction(success)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (error && !ax.isFunction(error)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (progress && !ax.isFunction(progress)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }

        var opts = {
            'method':'GET',
            'url':url,
            'headers':{},
            'params':{},
            'files':{},
            'download':path,
            'success':success,
            'error':error,
            'received':progress
        };
        return this.curl(opts);
    }

    /**
     * "multipart/form-data" 형식으로 HTTP "POST" 요청을 수행하여 파일을 업로드.
     * <p>
     * 내부적으로는 {ax.ext.net.curl} 함수를 호출함.
     *
     * @param {string} url
     *      url 요청 URL(필수)
     * @param {object.&lt;string,string&gt;} params
     *      요청 파라메터(선택).
     * @param {object.&lt;string,string&gt;} files
     *      파일 업로드 요청 파라메터(필수).
     * @param {ax.ext.net.CurlSuccessCallback} success
     *      요청이 성공했을때 호출될 콜백 함수(선택)
     * @param {ax.ext.net.CurlErrorCallback} error
     *      요청이 실패했을 때 호출될 콜백 함수(선택)
     * @param {ax.ext.net.CurlSentCallback} progress
     *      요청 송신 진행 상황에 따라 주기적으로 호출될 콜백 함수(선택)
     * @methodOf ax.ext.net
     *
     * @example
     * function fileUploadDemo(){
     *     var url = 'http://2do.kr/demo/upload.html',
     *         file = {'file1': 'downloads/icon.png'},
     *         params = {'param1':'param1 value'};
     *     var successCB = function(o){
     *         console.log('successCB:', o.status, o.data);
     *
     *     };
     *     var progressCB = function(progress){    // ax.ext.net.CurlSent
     *         console.log('progressCB:', progress[0], progress[1]);
     *     };
     *     var errorCB = function(e){
     *         console.log('progressCB:', e.message, e.code);
     *     };
     *
     *     ax.ext.net.upload(url, params, file, successCB ,errorCB, progressCB);
     * }
     */
    function upload(url, params, files, success, error, progress) {
        // validate params
        if (!ax.isString(url)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (params && !ax.isObject(params)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (files && !ax.isObject(files)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (success && !ax.isFunction(success)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (error && !ax.isFunction(error)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (progress && !ax.isFunction(progress)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }

        var opts = {
            'method':'POST',
            'url':url,
            'headers':{'Content-Type': 'multipart/form-data'},
            'params':params || {},
            'files':files || {},
            'success':success,
            'error':error,
            'sent':progress
        };
        return this.curl(opts);
    }

    /**
     * {ax.ext.net.sendMail}이 성공했을 때 호출될 콜백 함수.
     *
     * @class
     * @name SendMailSuccessCallback
     * @memberOf ax.ext.net
     * @see ax.ext.net.sendMail
     */

    /**
     * {ax.ext.net.sendMail}이 실패했을 때 호출될 콜백 함수.
     *
     * @class
     * @name SendMailErrorCallback
     * @memberOf ax.ext.net
     * @see ax.ext.net.sendMail
     */

    /**
     * {ax.ext.net.sendMail} 호출에 필요한 메일 정보가 담긴 객체.
     *
     * @class
     * @name SendMailOpts
     * @property {string} subject
     *      메일 제목
     * @property {string} message
     *      메일 본문
     * @property {array.&lt;string&gt;} to
     *      수신 목록(선택)
     * @property {array.&lt;string&gt;} cc
     *      참조 목록(선택)
     * @property {array.&lt;string&gt;} bcc
     *      숨은 참조 목록(선택)
     * @property {array.&lt;string&gt;} attachments
     *      첨부 파일 목록(선택). 파일 경로는 deviceapis.filesystem과 동일한 가상 절대 경로.
     * @memberOf ax.ext.net
     * @see ax.ext.net.sendMail
     */

    /**
     * 플랫폼의 기본 메일 클라이언트(MUA; Mail User Agent)를 이용하여 메일을 보냄.
     * <p>
     * 주의: 안드로이드의 경우 세부 동작에 단말에 따라 다름.
     *
     * @param {ax.ext.net.SendMailSuccessCallback} callback
     *      성공했을 때 호출될 콜백 함수.
     *      주의: 메일이 수신자에게 배달되었음을 보장하지 않음.
     * @param {ax.ext.net.SendMailErrorCallback} errback
     *      실패했을 때 호출될 콜백 함수.
     * @param {ax.ext.net.SendMailOpts} opts
     *      메일 전송에 필요한 정보.
     * @methodOf ax.ext.net
     *
     * @example
     * var options = {
     *     'subject':'mail title',
     *     'message':'message body',
     *     'to': ['EMAIL@ADDRESS'] //['abc@abc.com','bcd@bcd.com']
     * };
     * var scb = function(){
     *     console.log('scb called');
     * };
     * var ecb = function(e){
     *     console.log('ecb: ', e.message, e.code);
     * };
     *
     * ax.ext.net.sendMail(scb, ecb, options);
     */
    function sendMail(callback, errback, opts) {
        // validate params
        if (callback && !ax.isFunction(callback)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (errback && !ax.isFunction(errback)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (!ax.isObject(opts)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.subject && !ax.isString(opts.subject)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.message && !ax.isString(opts.message)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.to && !ax.isArray(opts.to)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.cc && !ax.isArray(opts.cc)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.bcc && !ax.isArray(opts.bcc)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }
        if (opts.attachments && !ax.isArray(opts.attachments)) { throw ax.error(ax.TYPE_MISMATCH_ERR); }

        var params = {
                subject: opts.subject || '',
                message: opts.message || '',
                to: opts.to || [],
                cc: opts.cc || [],
                bcc: opts.bcc || [],
                attachments: opts.attachments || []
        };

        function callbackWrapper() {
            ax.bridge.removeListener(params.listener);
            if(callback) {
                callback.apply(null, arguments);
            }
        }

        function errbackWrapper() {
            ax.bridge.removeListener(params.listener);
            if(errback) {
                errback.apply(null, arguments);
            }
        }

        params.listener = ax.bridge.addListener(callbackWrapper, errbackWrapper);

        return this.execAsync('sendMail', ax.nop, errbackWrapper, [ params ]);
    }

    //-------------------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'curl': curl,
        'get': get,
        'post': post,
        'download': download,
        'upload': upload,
        'sendMail': sendMail
    }, PLUGIN_NS);
}(window));
