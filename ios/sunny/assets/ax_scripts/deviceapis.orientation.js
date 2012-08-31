////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Orientation Module. <p>이 API는 단말의 방향계 정보를 제공합니다. 방향계 센서는 단말의 회전 방향 정보를 세 종류의 축으로 나누어 제공하는 센서입니다.각 축은 accelerometer 모듈과 동일하게 사용됩니다.<br>
 * x축: x축은 스크린에 수평이며 오른쪽을 향해 양수입니다.<br>
 * y축: y축은 스크린에 수평이며 상단을 향해 양수입니다.<br>
 * z축: z축은 스크린에 수직이며 위쪽으로 양수입니다.각 축에서 제공하는 정보는 다음과 같습니다.<br>
 * • <b>Alpha</b> (또는 azimuth) : Alpha는 z축을 중심으로 자북 방향과 장치의 y축 사이의 회전각입니다. 반 시계 방향으로 각도 단위로 측정되며 0과 360 사이의 값입니다.<br>
 * • <b>Beta</b> (또는 pitch) : Beta는 단말이 스크린을 위로 하여 평면에 놓여 있을 때 x축을 중심으로 y축과 장치의 y축 사이의 회전각입니다. 반 시계 방향으로 각도 단위로 측정되며 -180과 180 사이의 값입니다.<br>
 * • <b>Gamma</b> (또는 roll) : Gamma는 단말이 스크린을 위로 하여 평면에 놓여 있을 때 단말의 y축을 중심으로 x축과 단말의 x축 사이의 회전각입니다. 반 시계 방향으로 각도 단위로 측정되며 -90과 90 사이의 값입니다.<br>
 * ※ <a href="http://dev.w3.org/geo/api/spec-source-orientation.html">W3C DeviceOrientation Event Specification</a> 참고<br>
 * 각도는 항상 회전축의 양의 방향에서 바라보았을 때 반 시계 방향으로 측정됩니다. 예를 들어 단말이 스크린을 위로 하여 평면에 놓여져 있고 스크린의 상단이 북쪽을 향할 때 alpha, beta, gamma는 모두 0 입니다. 그 상태에서 단말을 회전시켜 스크린의 상단이 서쪽을 가리키게 되면 alpha의 값은 90이 됩니다.<br>
 * 이 API는 현재의 방향계 값을 제공할 뿐 아니라 센서에 의해 단말 방향의 변화가 감지되었을 때 이에 대한 통지 이벤트를 전달 받을 수 있습니다.</p>
 * <p>
 * http://wacapps.net/api/orientation 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 Orientation 인터페이스의 인스턴스가 deviceapis. orientation 으로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/orientation</strong><br>
 * 방향 센서의 정보에 접근합니다. 이 피쳐는 이 모듈의 모든 인터페이스, 메소드, 속성에 관련됩니다.<br><br>
 * ※ Appspresso는 WAC 위젯 외에도 Android와 iOS 앱을 개발할 수 있도록 하기 위해 config.xml 대신 플랫폼 중립적인 메타 정보를 정의할 수 있도록 project.xml 파일을 제공합니다. Appspresso에서 개발한 앱을 WAC 위젯으로 내보낼 때 Appspresso SDK는 project.xml 파일을 기초로 config.xml 파일을 자동 생성하여 WAC 위젯에 포함시킵니다. 이 문서에서는 WAC의 Waikiki API를 설명하기 위해 config.xml에 대한 설명을 그대로 옮깁니다.<br>
 * </p>
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');
    var deviceapis = g.deviceapis;// require('deviceapis');

    ////////////////////////////////////////////////////////////////////////////
    // convenient functions, variables

    var INTERVAL = 100;
    var g_watchIDs = {};
    var g_watchID = 20000;    // orientation: 20000 ~ 29999

    ////////////////////////////////////////////////////////////////////////////
    // interface Rotation

    /**
     * <p>단말의 방향. 이 인터페이스는 watchOrientation 함수와 getCurrentOrientation 함수가 성공한 경우 콜백으로 호출되는 successCallback 함수의 매개변수로 전달됩니다.</p>
     *
     * @class 단말의 방향
     * @name Rotation
     * @property {long} alpha <p>z축을 중심으로 한 회전각이며 반 시계 방향으로 양수입니다. 이 속성은 읽기 전용입니다.</p>
     * @property {long} beta <p>x축을 중심으로 한 회전각이며 반 시계 방향으로 양수입니다. 이 속성은 읽기 전용입니다.</p>
     * @property {long} gamma <p>y축을 중심으로 한 회전각이며 반 시계 방향으로 양수입니다. 이 속성은 읽기 전용입니다.</p>
     *
     * @example
     * function showOrientationInfo(rotation)
     * {
     *   alert("The orientation on the three axis is: "+
     *         rotation.alpha + " (alpha), " +
     *         rotation.beta + " (beta), " +
     *          rotation.gamma + " (gamma)");
     * }
     * var pOp =
     *   deviceapis.orientation.getCurrentOrientation(showOrientationInfo);
     */
    function Rotation(rotation) {
        ax.def(this)
            .constant('alpha', rotation.alpha)
            .constant('beta', rotation.beta)
            .constant('gamma', rotation.gamma);
    }

    ////////////////////////////////////////////////////////////////////////////
    // interface Orientation

    /**
     * <p>방향계 센서의 값을 제공합니다. 이 인터페이스는 방향 센서의 정보를 제공하며 DeviceapisOrientation 인터페이스에 의해 deviceapis 객체의 속성으로 제공됩니다.</p>
     * @class 방향계 센서의 값을 제공합니다
     * @name Orientation
     *
     * @example
     * var neworientation = 'flat';
     * var oldorientation = 'flat';
     *
     * function orientationChange(rotation)
     * {
     *   azimuth = rotation.alpha;
     *   pitch = rotation.beta;
     *   roll = rotation.gamma;
     *   if (pitch < -45)
     *     if(pitch > -135)
     *       neworientation = 'bottom';
     *   else if (pitch > 45)
     *     if (pitch < 135)
     *     neworientation = 'top';
     *   else if (roll > 45)
     *     neworientation = 'right';
     *   else if (roll  -45)
     *     neworientation = 'left';
     *   else
     *     neworientation = 'flat';
     *
     *  if (neworientation != oldorientation) {
     *    alert('Your orientation has changed');
     *    oldorientation = neworientation;
     *  }
     *
     * function genericError(e)
     * {
     *   alert("Error: "+e.message);
     * }
     *
     * var myOptions = {minNotificationInterval:10000}; //10 seconds
     * deviceapis.orientation.watchOrientation(orientationChange,
     *                                              genericError,
     *                                                myOptions);
     */
    function Orientation() {
    }

    // properties/methods of Orientation
    var GET_CURRENT_ORIENTATION = 'getCurrentOrientation';
    var WATCH_ORIENTATION = 'watchOrientation';
    var CLEAR_WATCH = 'clearWatch';


    /**
     * <p>단말의 현재 방향 값을 요청합니다.이 함수는 호출되면 즉시 제어권을 반환한 후에 현재의 방향 값을 얻는데 성공하면 Rotation 객체를 매개변수로 하여 successCallback 함수를 호출합니다. Rotation 객체는 현재의 방향 값을 포함하고 있습니다.
     * 방향 값을 얻는데 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * </p>
     *
     * @param {OrientationSuccessCallback} successCallback <p>방향 값을 얻는데 성공한 경우 콜백으로 호출됩니다. Appspresso의 Android 런타임의 경우 alpha값을 구하지 못하면 0으로 설정됩니다.</p>
     * @param {ErrorCallback} errorCallback <p>방향 값을 얻는데 실패한 경우 콜백으로 호출됩니다.</p>
     * @memberOf Orientation#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * // One-shot request
     * function showOrientationInfo(rotation)
     * {
     *   alert("Device orientation " + rotation.alpha + "," +
     *                 rotation.beta + "," + rotation.gamma);
     * }
     * var pOp = deviceapis.orientation.getCurrentOrientation(
     *                                     showOrientationInfo);
     */
    function getCurrentOrientation(successCallback, errorCallback) {
        ax.util.validateRequiredFunctionParam(successCallback);
        function scb(result) {
            ax.util.invokeLater(null, successCallback, new Rotation(result));
        }

        ax.util.validateCallback(successCallback, errorCallback, true);
        return this.execAsyncWAC(GET_CURRENT_ORIENTATION, scb, errorCallback);
    }

    /**
     * <p>방향 정보의 변경을 지속적으로 통지 받기 위해 이벤트 구독을 시작합니다. 이 함수가 호출되면 이벤트 구독 식별자를 즉시 반환한 후에 방향계 센서의 변화를 감시하는 작업을 비동기적으로 시작합니다. 방향계 센서를 감시하는 작업이 시작되면 주기적으로 방향 정보를 포함하는 Rotation 객체를 매개변수로 하여 successCallback 함수가 호출됩니다.
     * 만약 방향계 센서를 감시하는 작업이 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수가 호출됩니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * </p>
     *
     * @param {OrientationSuccessCallback} successCallback <p>방향 값을 얻는데 성공한 경우 콜백으로 호출됩니다. Appspresso의 Android 런타임의 경우 alpha값을 구하지 못하면 0으로 설정됩니다</p>
     * @param {ErrorCallback} errorCallback <p>방향 값을 얻는데 실패한 경우 콜백으로 호출됩니다.</p>
     * @param {OrientationOptions} options
     * @memberOf Orientation#
     * @type loing
     * @returns {long} <p>이벤트 통지 간격을 제어하기 위한 매개변수. 이벤트 구독 식별자</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * var alpha=0;
     *
     * // Watch orientation changes
     * function showOrientation(rotation)
     * {
     *   delta = orientation.alpha - alpha;
     *   alert("Azimuth has changed in " + delta + " degrees");
     *   alpha=orientation.alpha;
     * }
     *
     * function genericError(e)
     * {
     *   alert("Error: "+e.message);
     * }
     * var myOptions = {};
     * myOptions.minNotificationInterval = 10000; // 10 seconds freq.
     * var subId = deviceapis.orientation.watchOrientation(showOrientation,
     *                                            genericError, myOptions);
     */
    function watchOrientation(successCallback, errorCallback, options) {
        ax.util.validateRequiredFunctionParam(successCallback);
        ax.util.validateOptionalObjectParam(options);
        if (options) {
            ax.util.validateOptionalNumberParam(options.minNotificationInterval);
        }

        var interval, self = this, watchID = g_watchID++;
        interval = (options && options.minNotificationInterval) ? options.minNotificationInterval - 0 : INTERVAL;

        (function() {
            var timerID, _watchID = watchID, scb = successCallback, ecb = errorCallback;

            var first = true;

            // minNotificationInterval과 상관없이 최초의 값은 바로 가져와야 함
            try {
                var result = self.execSyncWAC(GET_CURRENT_ORIENTATION, [_watchID, first]);
                first = false;
                ax.util.invokeLater(null, scb, new Rotation(result));
            }
            catch (e) {
                self.errorAsyncWAC(e, ecb);
            }

            timerID = window.setInterval(function() {
                try {
                    var result = self.execSyncWAC(GET_CURRENT_ORIENTATION, [_watchID, first]);
                    first = false;
                    ax.util.invokeLater(null, scb, new Rotation(result));
                }
                catch (e) {
                    self.errorAsyncWAC(e, ecb);
                }
            }, interval);
            g_watchIDs[_watchID] = timerID;
        })();

        return watchID;

    //    var watchID;
    //
    //    function scb(result) {
    //        g_watchIDs[watchID] = null;
    //        ax.util.invokeLater(null, successCallback, new Rotation(result));
    //    }
    //
    //    return watchID = this.watchWAC('watchOrientation', scb, errorCallback, [options || {}]);
    }

    /**
     * <p>방향계 센서의 변화를 통지 받는 이벤트 구독을 중지합니다. 이 함수가 호출되면 watchOrientation 함수의 호출로 시작된 방향계 센서 감시 작업과 이벤트 통지 구독이 중지됩니다. 매개변수 watchId는 이전의 wathOrientation 함수 호출에서 반환 받은 이벤트 구독 식별자입니다.</p>
     *
     * @param {long} watchID <p>이벤트 구독 식별자</p>
     * @memberOf Orientation#
     * @type void
     * @returns {void}
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * function showOrientation(rotation)
     * {
     *   alert("New orientation value available");
     * }
     * function genericError(e)
     * {
     *   alert("Error: cancelling watch procedure");
     *   deviceapis.orientation.clearWatch(subId);
     * }
     * // Watch orientation changes
     * var subId = deviceapis.orientation.watchOrientation(showOrientation,
     *                                                     genericError);
     */
    function clearWatch(watchID) {
        ax.util.validateRequiredNumberParam(watchID);

        if (!(watchID in g_watchIDs)) {
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR, 'invalid watchID');
        }

        window.clearInterval(g_watchIDs[watchID]);
        delete g_watchIDs[watchID];
        try{
            this.execSyncWAC(CLEAR_WATCH, [watchID - 0]);
        }catch(e){
            //XXX: HOT FIX: watch 중 오류가 있는 경우 self.errorAsyncWAC(e, ecb); 된 에러콜백이 throw됨. 왜그런지 모르겠음 ㅠㅠ - althjs
        }

        // if (watchID in g_watchIDs) {
        // this.stopWatchWAC('clearWatch', watchId);
        // delete g_watchIDs[watchID];
        //    }
    }

    ////////////////////////////////////////////////////////////////////////////
    // clear watch on unload

    window.addEventListener('unload', function() {
        ax.util.foreach(g_watchIDs, function(watchID, v) {
            try {
                orientation.execSyncWAC(CLEAR_WATCH, [ watchID - 0 ]);
                // orientation.stopWatchWAC('clearWatch', watchID - 0);
            }
            catch (ignored) {
            }
        });
    }, false);

    Orientation.prototype = ax.plugin('deviceapis.orientation', {
        'getCurrentOrientation': getCurrentOrientation,
        'watchOrientation': watchOrientation,
        'clearWatch': clearWatch
    });

    ax.def(g).constant('Orientation', Orientation);
    ax.def(deviceapis).constant('orientation', new Orientation());
    // ====================================================
}(window));

/**
 * <p>방향센서의 변화를 통지하는 이벤트 구독과 관련된 선택 사항을 지정합니다. 이 인터페이스는 watchOrientation 함수에서 successCallback 함수의 콜백 호출 조건을 지정하기 위해 사용됩니다</p>
 * @class 방향센서의 변화를 통지하는 이벤트 구독과 관련된 선택 사항을 지정합니다
 * @name OrientationOptions
 */

/**
 * <p>통지 이벤트 사이의 최소 간격. 이 값이 클수록 통지 이벤트의 발생 빈도가 줄어듭니다. watchOrientation 함수 호출로 발생하는 통지 이벤트들 사이의 최소 간격을 milliseconds단위로 지정합니다.</p>
 *
 * @filed
 * @type long
 * @name minNotificationInterval
 * @memberOf OrientationOptions#
 *
 * @example
 * function showOrientationInfo(rotation)
 * {
 *   alert("New orientation info available");
 * }
 * function genericError(e)
 * {
 *   alert("Error: "+e.message);
 * }
 * var myOptions = {};
 * myOptions.minNotificationInterval = 10000; // 10 seconds
 * var subId = deviceapis.orientation.watchOrientation(showOrientationInfo,
 *                                                     genericError,
 *                                                     myOptions);
 */


/**
 * <p>방향계의 성공 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name OrientationSuccessCallback
 */

/**
 * <p>비동기 호출이 성공한 경우 호출됩니다.</p>
 * @function
 * @param {Rotation} rotation <p>단말의 방향</p>
 * @return {void}
 * @memberOf OrientationSuccessCallback
 * @name onsuccess
 */
