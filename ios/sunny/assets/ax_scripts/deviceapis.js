/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Deviceapis Module. <p>이 모듈에서는 성공과 실패를 처리하기 위한 일반적인 콜백 인터페이스, GenericError 인터페이스, 비동기 호출을 취소하기 위한 PendingOperation 인터페이스 등과 같이 다른 WAC 모듈들에서 공통적으로 사용되는 기본적인 인터페이스들을 정의합니다. 또한 다른 모듈들에 접근할 수 있는 통로로서의 루트 모듈을 정의하며 WAC 런타임에서 지원하는 피쳐들과 활성화된 피쳐들의 목록을 가져올 수 있는 방법을 제공합니다.</p>
 * <p>
 * http://wacapps.net/api/deviceapis 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 Deviceapis인터페이스의 인스턴스가 window.deviceapis로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/deviceapis</strong> - Device API의 기본 모듈에 접근합니다.
 * </p>
 */

(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    ////////////////////////////////////////////////////////////////////////////
    // typedef

    ax.def(g)
        .constant('ParamArray', Array)
        .constant('UnsignedShortArray', Array)
        .constant('ShortArray', Array)
        .constant('FeatureArray', Array);

    ////////////////////////////////////////////////////////////////////////////
    // interface DeviceAPIError

    var CONSTRUCTOR_DEVICEAPIERROR = 'DeviceAPIError';

    /**
     * <p>범용적인 에러 인터페이스입니다. WAC의 모듈들 중 일부는 이 인터페이스를 확장하여 추가적인 에러 인터페이스를 정의합니다.이 인터페이스는 동기 호출에서 에러가 발생했을 때 던져지거나(throw) 비동기 호출에서 에러가 발생했을 때 에러 콜백 함수의 매개변수로 전달됩니다. 이 인터페이스의 에러 코드들은 DOM Level 3 명세에 몇몇 코드를 더해서 정의되었습니다. 다른 WAC 모듈에서 이 인터페이스를 확장해 추가적인 에러 인터페이스를 정의한 경우의 에러 코드는 미래의 DOM 명세에서 에러 코드들이 추가되는 것에 대비해 100부터 시작합니다. </p>
     *
     * @class 범용적인 에러 인터페이스입니다.
     * @name DeviceAPIError
     * @example
     * deviceapis.filesystem.resolve(
     *    function(dir) {
     *         // do something
     *    },
     *    function(e) {
     *      // handling error defined in deviceapis
     *      if (e.code == e.NOT_SUPPORTED_ERR) alert("not supported");
     *      // handling error defined in appropriate module (e.g. filesystem)
     *      if (e.code == e.IO_ERR) alert("i/o error");
     *    }, 'images', 'r'
     * );
     *
     */

    /**
     *  <p>읽기 전용의 16-bit 오류 코드.</p>
     *  @type  unsigned short
     *  @filed
     *  @memberOf DeviceAPIError#
     *  @name code
     */

    /**
     * <p>읽기 전용의 오류 메시지. 오류에 대한 상세한 내용을 설명합니다. 이 메시지는 사용자 인터페이스를 통해 최종 사용자에게 직접 보여지는 것보다는 개발자에게 유용한 정보를 전달하기 위한 용도로 제공됩니다.</p>
     * @type  DOMString
     * @filed
     * @memberOf DeviceAPIError#
     * @name message
     */

    /**
     * <p>알 수 없는 오류 에러 코드</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name UNKNOWN_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name INDEX_SIZE_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name DOMSTRING_SIZE_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name HIERARCHY_REQUEST_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name WRONG_DOCUMENT_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name INVALID_CHARACTER_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name NO_DATA_ALLOWED_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name NO_MODIFICATION_ALLOWED_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name NOT_FOUND_ERR
     */

    /**
     * <p>일반적인 에러 코드.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name NOT_SUPPORTED_ERR
     */

    /**
     * <p>WAC 런타임에서 요청된 객체의 형식이나 연산을 지원하지 않는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name INUSE_ATTRIBUTE_ERR
     */

    /**
     * <p>WAC 런타임에서 요청된 객체의 형식이나 연산을 지원하지 않는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name INVALID_STATE_ERR
     */

    /**
     * <p>WAC 런타임에서 요청된 객체의 형식이나 연산을 지원하지 않는 경우.</p>
     * @constant
     * @filed
     * @memberOf DeviceAPIError#
     * @name SYNTAX_ERR
     */

    /**
     * <p>WAC 런타임에서 요청된 객체의 형식이나 연산을 지원하지 않는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name INVALID_MODIFICATION_ERR
     */

    /**
     * <p>WAC 런타임에서 요청된 객체의 형식이나 연산을 지원하지 않는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name NAMESPACE_ERR
     */

    /**
     * <p>WAC 런타임에서 요청된 객체의 형식이나 연산을 지원하지 않는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name INVALID_ACCESS_ERR
     */

    /**
     * <p>WAC 런타임에서 요청된 객체의 형식이나 연산을 지원하지 않는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name VALIDATION_ERR
     */

    /**
     * <p>각 API 함수로 전달된 매개변수의 형식이 일치하지 않는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name TYPE_MISMATCH_ERR
     */

    /**
     * <p>보안정책에 위배되거나 보안상의 위험을 유발할 수 있는 데이터에 대한 접근, 연산을 수행하려는 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name SECURITY_ERR
     */

    /**
     * <p>동기 호출에서 네트워크 오류가 발생한 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name NETWORK_ERR
     */

    /**
     * <p></p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name ABORT_ERR
     */

    /**
     * <p>시간 초과로 요청된 작업이 완료되지 못한 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name TIMEOUT_ERR
     */

    /**
     * <p>객체가 유효한 값을 포함하고 있지 않은 경우.</p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name INVALID_VALUES_ERR
     */

    /**
     * <p></p>
     * @constant
     * @memberOf DeviceAPIError#
     * @name NOT_AVAILABLE_ERR
     */
    function DeviceAPIError(code, message, cause) {
        ax.AxError.apply(this, arguments);
        this.name = CONSTRUCTOR_DEVICEAPIERROR;
    }
    DeviceAPIError.constructor = ax.AxError;
    DeviceAPIError.prototype = new ax.AxError;
    DeviceAPIError.toString = function() { return 'function DeviceAPIError() { [native code]  }'; };

    ax.def(g).constant(CONSTRUCTOR_DEVICEAPIERROR, DeviceAPIError);

    ////////////////////////////////////////////////////////////////////////////
    // ax.util 확장

    /**
     * @deprecated use <code>new DeviceAPIError(...);</code>
     */
    function createDeviceAPIError(code, message, cause) {
        return new DeviceAPIError(code, message, cause);
    }

    function validateRequiredStringParam(param) {//mandatory & !nullable
        if (!ax.isString(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateOptionalStringParam(param) {//!mandatory & nullable
        if (param && !ax.isString(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateRequiredNumberParam(param) {//mandatory & !nullable
        if (!ax.isNumber(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateOptionalNumberParam(param) {//!mandatory & nullable
        if (param && !ax.isNumber(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateRequiredBooleanParam(param) {//mandatory & !nullable
        if (!ax.isBoolean(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateOptionalBooleanParam(param) {//!mandatory & nullable
        if (param && !ax.isBoolean(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateRequiredObjectParam(param) {//mandatory & !nullable
        if (!ax.isObject(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateOptionalObjectParam(param) {//!mandatory & nullable
        if (param && !ax.isObject(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateRequiredArrayParam(param) {//mandatory & !nullable
        if (!ax.isArray(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateOptionalArrayParam(param) {//!mandatory & nullable
        if (param && !ax.isArray(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateRequiredFunctionParam(param) {//mandatory & !nullable
        if (!ax.isFunction(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateOptionalFunctionParam(param) {//!mandatory & nullable
        if (param && !ax.isFunction(param)) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); };
    }

    function validateParamWAC(param, mandatory, nullable, type, exception, name) {
        try {
            return ax.util.validateParam(param, mandatory, nullable, type, exception, name);
        } catch (e) {
            throw new DeviceAPIError(e);
        }
    }

    function validateInstanceWAC(value, constructor, exception, name) {
        try {
            return ax.util.validateInstance(value, constructor, exception, name);
        } catch (e) {
            throw new DeviceAPIError(e);
        }
    }

    function validateCallback(scb, ecb, name) {
        if (!ax.isFunction(scb)) {
            throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR, 'invalid success callback');
        }

        if(ecb === null || ecb === undefined) {    // add no operation error callback with warning message - althjs
            return function(){
                    ax.console.warn('error callback was ignored from "' + name + '" method');
                };
        }

        if (!ax.isFunction(ecb)) {
            throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR, 'invalid error callback');
        }

           return ecb;
    }

    ax.def(ax.util)
        .method('createDeviceAPIError', createDeviceAPIError)
        .method('validateParamWAC', validateParamWAC)
        .method('validateInstanceWAC', validateInstanceWAC)
        .method('validateCallback', validateCallback)
        .method('validateOptionalStringParam',validateOptionalStringParam)
        .method('validateRequiredStringParam',validateRequiredStringParam)
        .method('validateOptionalNumberParam',validateOptionalNumberParam)
        .method('validateRequiredNumberParam',validateRequiredNumberParam)
        .method('validateOptionalArrayParam',validateOptionalArrayParam)
        .method('validateRequiredArrayParam',validateRequiredArrayParam)
        .method('validateOptionalObjectParam',validateOptionalObjectParam)
        .method('validateRequiredObjectParam',validateRequiredObjectParam)
        .method('validateOptionalFunctionParam',validateOptionalFunctionParam)
        .method('validateRequiredFunctionParam',validateRequiredFunctionParam)
        .method('validateOptionalBooleanParam',validateOptionalBooleanParam)
        .method('validateRequiredBooleanParam',validateRequiredBooleanParam);


    ////////////////////////////////////////////////////////////////////////////
    // interface PendingOperation

    var CONSTRUCTOR_PENDING_OPERATION = 'PendingOperation';

    /**
     * <p>비동기 호출의 연산에 대응되는 객체입니다. 이 인터페이스는 비동기 호출을 취소할 수 있도록 하기 위해 비동기 호출에 의해 반환됩니다.</p>
     * @class 비동기 호출의 연산에 대응되는 객체입니다.
     * @name PendingOperation
     * @example
     * var pendingOp = null;
     *
     * // SMS sending example
     * var msg = deviceapis.messaging.createMessage(Messaging.MESSAGE_TYPE_SMS);
     * msg.body = "I will arrive in 10 minutes";
     * msg.destinationAddress[0] = "+34666666666";
     *
     * // Define the success callback
     * function messageSent() {
     *   alert("The SMS has been sent");
     *   pendingOp = null;
     * }
     *
     * // Define the error callback
     * function messageFailed(error) {
     *   alert("The SMS could not be sent " + error.message);
     *   pendingOp = null;
     * }
     *
     * // To be executed if, for instance, the user presses a cancel button in the user interface
     * function cancel() {
     *   if (pendingOp != null) {
     *     if (pendingOp.cancel()) {
     *       alert("The message sending has been canceled");
     *     } else {
     *       alert("The operation cannot be canceled");
     *     }
     *   } else {
     *     alert("The operation cannot be canceled");
     *   }
     * }
     *
     * pendingOp = deviceapis.messaging.send(msg, messageSent, messageFailed);
     *
     */
    function PendingOperation(asyncOperation) {
        if (!ax.isRequest(asyncOperation)) {
            throw new TypeError('Illegal constructor');
        }

        /**
         * <p>비동기적으로 수행 중인 연산을 취소합니다. 이 호출은 실패 없이 항상 성공합니다. 비동기 호출이 취소된 경우는 물론 비동기 호출이 이미 종료되어 취소할 수 없는 경우에도 실패하지 않습니다. </p>
         * @function
         * @type boolean
         * @memberOf PendingOperation#
         * @returns {boolean} <p>비동기 작업이 이미 종료되었거나 기술적 제약으로 비동기 작업을 취소시킬 수 없는 경우 false를 반환합니다. 이 경우 비동기 호출의 결과가 관련된 콜백 함수로 전달되어 호출됩니다. 비동기 작업을 취소시킨 경우 true를 반환합니다. 이 경우 비동기 호출과 관련된 콜백 함수가 호출되지 않습니다.</p>
         * @name cancel
         */
        this.cancel = function() {
            return asyncOperation.cancel();
        };
    }

    // window.PendingOperation
    ax.def(g).constant(CONSTRUCTOR_PENDING_OPERATION, PendingOperation);

    ////////////////////////////////////////////////////////////////////////////
    // interface Feature

    var CONSTRUCTOR_FEATURE = 'Feature';

    /**
     * <p>피쳐에 대한 정보를 제공합니다. 이 인터페이스는 피쳐에 대한 자세한 정보를 제공합니다.</p>
     * @class 피쳐에 대한 정보를 제공합니다.
     * @name Feature
     * @property {DOMSTring} uri 피쳐의 URI. 각 피쳐마다 정의된 고유 ID입니다.
     * @property {boolean} required <p>필수 표식(flag). 위젯의 설정파일(config.xml)에서 필수로 표시한 피쳐의 경우 이 속성이 true로 설정됩니다.</p>
     * @property {ParamArray} params <p>피쳐의 매개변수. 이 속성은 위젯의 설정파일(config.xml)에서 해당 피쳐에 정의한 모든 매개변수의 배열입니다. <br>
     * ※ Appspresso 1.0 beta에서 지원하는 Waikiki API 2.0 beta의 경우 이 속성에 대해 기술한 명세에 오류가 있어 지원하지 않습니다. 이 오류는 Waikiki API 2.0 정식 버전에서 정정되었습니다.</p>
     */
    function Feature(uri, required, params) {
        this.uri = uri;
        this.required = required;
        this.params = params;
    }

    // window.Feature
    ax.def(g).constant(CONSTRUCTOR_FEATURE, Feature);

    ////////////////////////////////////////////////////////////////////////////
    // ax.bridge 확장
    // wrap error(AxError, Error) into DeviceAPIError
    // wrap AsyncOperation into PendingOperation
    // error callback이 null인 경우에도 async call이 error로 끝났음을 log로 알린다

    function execSyncWAC(method, params) {
        try {
            return this.execSync(method, params);
        } catch (e) {
            throw new DeviceAPIError(e);
        }
    }

    function watchWAC(method, successCallback, errorCallback, params) {
        validateCallback(successCallback, errorCallback);

        try {
            return this.watch(method, successCallback, errorCallback, params);
        }
        catch (e) {
            throw new DeviceAPIError(e);
        }
    }

    function stopWatchWAC(method, watchID) {
        try {
            return this.stopWatch(method, watchID);
        }
        catch (e) {
            throw new DeviceAPIError(e);
        }
    }

    function execAsyncWAC(method, successCallback, errorCallback, params) {

        validateRequiredFunctionParam(successCallback);
        validateOptionalFunctionParam(errorCallback);

        // 파라메터로 넘어오는 errorCallback을 참조해야 하므로...
        // 여기에 로컬 함수로 선언해야 함... -_-
        function errorCallbackWrapper(error) {
            errorAsyncWAC(error, errorCallback);
        }

        try {
            return new PendingOperation(this.execAsync(method, successCallback, errorCallbackWrapper, params));
        } catch (e) {
            throw new DeviceAPIError(e);
        }
    }

    function errorAsyncWAC(error, errorCallback){
        // 타입불일치는 에러 콜백 유무에 상관없이 예외를 지금 바로! "던진다".
        if(ax.isNumber(error) && error === ax.TYPE_MISMATCH_ERR) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); }
        if(ax.isError(error) && error.code === ax.TYPE_MISMATCH_ERR) { throw error; }
        // 에러 콜백이 없으면 경고만 찍고 무시~
        if(!errorCallback) {
            ax.console.warn('error callback was ignored: error=' + error);
            return;
        }

        // 에러 콜백에게 에러를 넘겨준다.
        ax.util.invokeLater(null, errorCallback, new DeviceAPIError(error));
    }

    // TODO: extends ax.AxPlugin for WAC apis...
    ax.def(ax.AxPlugin.prototype)
        .method('execSyncWAC', execSyncWAC)
        .method('execAsyncWAC', execAsyncWAC)
        .method('watchWAC', watchWAC)
        .method('stopWatchWAC', stopWatchWAC)
        .method('errorAsyncWAC',errorAsyncWAC);

    ////////////////////////////////////////////////////////////////////////////
    // interface Deviceapis

    var DEVICEAPIS = 'deviceapis';
    var CONSTRUCTOR_DEVICEAPIS = 'Deviceapis';

    var LIST_AVAILABLE_FEATURES = 'listAvailableFeatures';
    var LIST_ACTIVATED_FEATURES = 'listActivatedFeatures';

    /**
     * <p>WAC의 루트 API. 이 인터페이스는 WAC의 루트 인터페이스로 모든 WAC 모듈들은 이 인터페이스를 통해 참조할 수 있습니다 (e.g. deviceapis.camera, deviceapis.filesystem, …). 이 인터페이스는 DeviceapisObject 인터페이스에 의해 전역 객체 window의 deviceapis 속성으로 제공됩니다.</p>
     * @class WAC의 루트 API
     * @name Deviceapis
     */
    function Deviceapis() {
    }

    /**
     * <p>WAC을 구현한 런타임에서 지원하는 피쳐들의 목록을 반환합니다. 런타임에서 지원하는 피쳐들의 목록을 제공합니다. 위젯의 설정파일(cofig.xml)에 기재되지 않은 피쳐의 경우 required 속성과 params 속성은 null 값을 갖습니다. 위젯의 설정파일에 기재된 피쳐의 경우 required 속성과 params 속성의 값은 설정 파일에 정의된 값을 갖습니다.<br>
     * ※  Appspresso는 WAC 위젯 외에도 Android와 iOS 앱을 개발할 수 있도록 하기 위해 config.xml 대신 플랫폼 중립적인 메타 정보를 정의할 수 있도록 project.xml 파일을 제공합니다. Appspresso에서 개발한 앱을 WAC 위젯으로 내보낼 때 Appspresso SDK는 project.xml 파일을 기초로 config.xml 파일을 자동 생성하여 WAC 위젯에 포함시킵니다. 이 문서에서는 WAC의 Waikiki API를 설명하기 위해 config.xml에 대한 설명을 그대로 옮깁니다.</p>
     *
     * @memberOf Deviceapis#
     * @type FeatureArray
     * @returns {FeatureArray} <p>런타임에서 지원하는 모든 피쳐들의 배열. 오류가 발생한 경우에는 null을 반환합니다.</p>
     * @example
     *
     * var features = deviceapis.listAvailableFeatures();
     * for (var i=0; i < features.length; i++) {
     *   alert("The Feature " + features[i].uri + " is supported");
     * }
     *
     */
    function listAvailableFeatures() {
        var result = [];
        try {
            ax.util.foreach(this.execSyncWAC(LIST_AVAILABLE_FEATURES), function(k, v) {
                result.push(new Feature(v.uri, v.required, v.params));
            });
        }
        catch (e) {
            if(_DEBUG) { ax.debug('listAvailableFeatures failed, return null. {0}', e); }
            // Waikiki API spec: null in case an error occurs
            // TODO throw new DeviceAPIError(e) Waikiki API 2.0에서 변경되는지 점검할 것
        }
        return result;
    }

    /**
     * <p>위젯의 설정파일(config.xml)에 기재되어 런타임에 의해 활성화된 피쳐들의 목록을 제공합니다. 설정파일을 통해 위젯에 의해 요청되고 런타임에 의해 활성화된 피쳐들의 목록을 제공합니다. 각 피쳐들의 속성은 위젯의 설정파일에 기재된 값과 동일합니다.</p>
     *
     * @memberOf Deviceapis#
     * @type FeatureArray
     * @returns {FeatureArray} <p>활성화 된 피쳐들의 배열. 오류가 발생한 경우에는 null을 반환합니다</p>
     *
     * @example
     * var features = deviceapis.listActivatedFeatures();
     * for (var i=0; i < features.length; i++) {
     *   alert("The Feature " + features[i].uri + " has been activated");
     * }
     */
    function listActivatedFeatures() {
        var result = [];
        try {
            ax.util.foreach(this.execSyncWAC(LIST_ACTIVATED_FEATURES), function(k, v) {
                result.push(new Feature(v.uri, v.required, v.params));
            });
        }
        catch (e) {
            if(_DEBUG) { ax.debug('listAvailableFeatures failed, return null. {0}', e); }
            // Waikiki API spec: null in case an error occurs
            // TODO throw new DeviceAPIError(e) Waikiki API 2.0에서 변경되는지 점검할 것
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // non spec, for test functions

    function _testAsync(sc, ec, a, b, c) {
        return this.execAsyncWAC('_testAsync', sc, ec, [a, b, c]);
    }

    function _testWatch(sc, ec) {
        return this.watchWAC('_testWatch', sc, ec, []);
    }

    function _testClearWatch(id) {
        this.stopWatchWAC('_testClearWatch', id);
    }

    // ====================================================
    Deviceapis.prototype = ax.plugin(DEVICEAPIS, {
        '_testAync': _testAsync,
        '_testWatch': _testWatch,
        '_testClearWatch': _testClearWatch,
        'listAvailableFeatures': listAvailableFeatures,
        'listActivatedFeatures': listActivatedFeatures
    });
    ax.def(g)
        .constant(CONSTRUCTOR_DEVICEAPIS, Deviceapis)
        .constant(DEVICEAPIS, new Deviceapis());
    // ====================================================
}(window));

/**
 * <p>일반적인 오류 콜백 인터페이스입니다. 이 인터페이스는 비동기 호출이 실패한 경우 호출되는 함수가 매개변수로 단지 오류 객체만을 필요로 할 경우 사용할 수 있는 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name ErrorCallback
 */

/**
 * <p>비동기 호출에서 오류가 발생한 경우 호출됩니다.</p>
 * @function
 * @param {DeviceAPIError} error <p>오류객체</p>
 * @return {void}
 * @memberOf ErrorCallback
 * @name onerror
 */

/**
 * <p>일반적인 성공 콜백 인터페이스. 이 콜백 인터페이스는 비동기 호출이 성공한 경우 호출되는 함수가 아무런 매개변수를 필요로 하지 않는 경우 사용할 수 있는 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name SuccessCallback
 */

/**
 * <p>비동기적 호출이 성공적으로 완료되면 호출됩니다.</p>
 * @function
 * @memberOf SuccessCallback
 * @name onsucces
 * @return {void}
 */
