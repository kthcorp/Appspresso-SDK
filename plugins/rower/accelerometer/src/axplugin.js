////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Accelerometer Module. <p>이 API는 단말의 가속도계 정보를 제공합니다. 가속도계는 단말의 가속 정보를 세 종류의 축으로 나누어 제공하는 센서입니다. 이 API는 현재의 가속도 값을 제공할 뿐 아니라 센서에 의해 가속도의 변화가 감지되었을 때 이에 대한 통지 이벤트를 전달 받을 수 있습니다.
 * <br>다음은 단말의 가속정보를 탐지하기 위한 세 종류의 축에 대한 설명입니다.
 * <dl style="margin-left:20px;margin-top:-10px;margin-bottom:10px;">
 * <dd style="display:list-item;list-style:disc;"><b>x축</b>: x축은 스크린에 수평이며 오른쪽을 향해 양수입니다.</dd>
 * <dd style="display:list-item;list-style:disc;"><b>y축</b>: y축은 스크린에 수평이며 상단을 향해 양수입니다.</dd>
 * <dd style="display:list-item;list-style:disc;"><b>z축</b>: z축은 스크린에 수직이며 위쪽으로 양수입니다.</dd>
 * </dl>
 * 가속은 m/s2 단위로 측정되며 가속 방향에 따라 각 축의 값이 결정됩니다. 중력은 가속도의 한 형태이며 스크린을 하늘 방향으로 하여 단말을 평면에 놓았을 때 z축의 값은 -9.8 m/s2입니다.
 * </p>
 * <p><br>
 * http://wacapps.net/api/accelerometer 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 Accelerometer인터페이스의 인스턴스가 deviceapis.accelerometer로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/accelerometer</strong><br>
 * 가속 센서의 정보에 접근합니다. 이 피쳐는 이 모듈의 모든 인터페이스, 메소드, 속성에 관련됩니다.<br><br>
 * ※  Appspresso는 WAC 위젯 외에도 Android와 iOS 앱을 개발할 수 있도록 하기 위해 config.xml 대신 플랫폼 중립적인 메타 정보를 정의할 수 있도록 project.xml 파일을 제공합니다. Appspresso에서 개발한 앱을 WAC 위젯으로 내보낼 때 Appspresso SDK는 project.xml 파일을 기초로 config.xml 파일을 자동 생성하여 WAC 위젯에 포함시킵니다. 이 문서에서는 WAC의 Waikiki API를 설명하기 위해 config.xml에 대한 설명을 그대로 옮깁니다.<br>
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
    var g_watchID = 10000;        // accelerometer: 10000 ~ 19999



    ////////////////////////////////////////////////////////////////////////////
    // interface Acceleration

    /**
     * <p>단말의 가속도. 이 인터페이스는 watchAcceleration 함수와 getCurrentAcceleration 함수가 성공한 경우 콜백으로 호출되는 successCallback 함수의 매개변수로 전달됩니다.</p>
     *
     * @class 단말의 가속도.
     * @name Acceleration
     * @property {float} xAxis m/s2 단위의 x축 가속도입니다. 단말의 오른쪽 방향으로 양수, 왼쪽 방향으로 음수입니다.이 속성은 읽기 전용입니다.
     * @property {float} yAxis m/s2 단위의 y축 가속도입니다. 단말의 상단 방향으로 양수, 하단 방향으로 음수입니다.이 속성은 읽기 전용입니다
     * @property {float} zAxis m/s2 단위의 z축 가속도입니다. 단말의 스크린 방향으로 양수, 단말의 뒷면 방향으로 음수입니다.이 속성은 읽기 전용입니다
     *
     * @example
     *  deviceapis.accelerometer(function(acceleration) {
     *      alert("Acceleration in x,y,z axis is: " +
     *             acceleration.xAxis + "," +
     *             acceleration.yAxis + "," +
     *             acceleration.zAxis);
     *    });
     *
     */
    function Acceleration(acc) {
        ax.def(this)
            .constant('xAxis', acc.xAxis)
            .constant('yAxis', acc.yAxis)
            .constant('zAxis', acc.zAxis);
    }

    ////////////////////////////////////////////////////////////////////////////
    // interface Accelerometer

    /**
     * <p>가속도계 센서의 값을 제공합니다. 이 인터페이스는 가속도계 센서의 정보를 제공하며 DeviceapisAccelerometer 인터페이스에 의해 deviceapis 객체의 속성으로 제공됩니다.</p>
     * @class 가속도계 센서의 값을 제공합니다.
     * @name Accelerometer
     */
    function Accelerometer() {
    }

    // properties/methods of Accelerometer
    var GET_CURRENT_ACCELERATION = 'getCurrentAcceleration';
    var WATCH_ACCELERATION = 'watchAcceleration';
    var CLEAR_WATCH = 'clearWatch';


     /**
      * <p>단말의 현재 가속 값을 요청합니다. 이 함수는 호출되면 즉시 제어권을 반환한 후에 현재의 가속 값을 얻는데 성공하면 Acceleration 객체를 매개변수로 하여 successCallback 함수를 호출합니다. Acceleration 객체는 현재의 가속 값을 포함하고 있습니다. 가속 값을 얻는데 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.
      * <ul>
      * <li>NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우</li>
      * <li>SECURITY_ERR: 이 연산이 허용되지 않는 경우</li>
      * <li>UNKNOWN_ERR: 그 밖에 다른 모든 경우</li>
      * </ul>
      *
      * @param {AccelerationSuccessCallback} successCallback <p>가속 값을 얻는데 성공한 경우 콜백으로 호출됩니다.</p>
      * @param {ErrorCallback} errorCallback <p>가속 값을 얻는데 실패한 경우 콜백으로 호출됩니다.</p>
      * @type PendingOperation
      * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
      * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
      * @memberOf Accelerometer.prototype
      *
      * @example
      * deviceapis.accelerometer.getCurrentAcceleration(function(acceleration) {
      *   if (acceleration.xAxis == 0)
      *     alert("The device is not moving in the xAxis");
      *   else
      *     alert("The device is moving in the xAxis");
      *   });
      */
    function getCurrentAcceleration(successCallback, errorCallback) {
        ax.util.validateRequiredFunctionParam(successCallback);
        function scb(result) {
            ax.util.invokeLater(null, successCallback, new Acceleration(result));
        }

        return this.execAsyncWAC(GET_CURRENT_ACCELERATION, scb, errorCallback);
    }

    /**
     * <p>가속 정보의 변경을 지속적으로 통지 받기 위해 이벤트 구독을 시작합니다. 이 함수가 호출되면 이벤트 구독 식별자를 즉시 반환한 후에 가속도계 센서의 변화를 감시하는 작업을 비동기적으로 시작합니다. 가속도계 센서를 감시하는 작업이 시작되면 주기적으로 가속 정보를 포함하는 Acceleration 객체를 매개변수로 하여 successCallback 함수가 호출됩니다. 만약 가속도계 센서를 감시하는 작업이 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수가 호출됩니다. 발생 가능한 에러 코드는 다음과 같습니다.
     * <br>•NOT_SUPPORTED_ERR : 이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR : 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR : 그 밖에 다른 모든 경우<br>가속 정보의 변화를 감시하는 작업은 지속적으로 수행되며 관련된 콜백 함수 역시 지속적으로 호출됩니다. 이벤트 구독을 중지하려면 반환 받은 구독 식별자를 매개변수로 하여 clearWatch 함수를 호출해야 합니다.통지 이벤트 사이의 최소 시간 간격을 options 매개변수의 minNotificationInterval 속성에 지정할 수 있습니다.
     * </p>
      *
     * @param {AccelerationSuccessCallback} successCallback <p>가속 값을 통지하기 위해 콜백으로 호출됩니다.</p>
     * @param {ErrorCallback} errorCallback <p>가속도계 센서를 감시하는 데 실패하면 콜백으로 호출됩니다.</p>
     * @param {AccelerationOptions} options <p>이벤트 통지 간격을 제어하기 위한 매개변수.</p>
     * @type void
     * @returns {long} <p>이벤트 구독 식별자</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     * @memberOf Accelerometer.prototype
      *
     * @example
      * // Last acceleration value retrieved
      * var x = 0;
      *
      * // Receives acceleration changes
      * function watcher(acceleration){
      *   alert("The acceleration in x Axis changed in " +
      *                         acceleration.xAxis - x + " m/s2");
      *   x = acceleration.xAxis;
      * }
      *
      * // registers to be notified when acceleration changes
      * // (minimum time between notifications is 10 secs)
      * deviceapis.accelerometer.watchAcceleration(watcher,
      *               function(error){alert("An error occurred");},
      *                        {minNotificationInterval:10000}  )
     */
    function watchAcceleration(successCallback, errorCallback, options) {
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
                var result = self.execSyncWAC(GET_CURRENT_ACCELERATION, [_watchID, first]);
                first = false;
                ax.util.invokeLater(null, scb, new Acceleration(result));
            } catch (e) {
                self.errorAsyncWAC(e, ecb);
            }

            timerID = window.setInterval(function() {
                try {
                    var result = self.execSyncWAC(GET_CURRENT_ACCELERATION, [_watchID, first]);
                    first = false;
                    ax.util.invokeLater(null, scb, new Acceleration(result));
                }
                catch (e) {
                    self.errorAsyncWAC(e, ecb);
                }
            }, interval);
            g_watchIDs[_watchID] = timerID;
        })();

        return watchID;
//        var watchID;
//
//        function scb(result) {
//            g_watchIDs[watchID] = null;
//            ax.util.invokeLater(null, successCallback, new Acceleration(result, KEY));
//        }
//
//        return watchID = this.watchWAC('watchAcceleration', scb, errorCallback, [options || {}]);
    }


    /**
     * <p>가속도계 센서의 변화를 통지하는 이벤트 구독과 관련된 선택 사항을 지정합니다.이 인터페이스는 watchAcceleration 함수에서 successCallback 함수의 콜백 호출 조건을 지정하기 위해 사용됩니다.</p>
     * @class 가속도계 센서의 변화를 통지하는 이벤트 구독과 관련된 선택 사항을 지정합니다.
     * @name AccelerationOptions
     *
     * @example
     * // Receives acceleration changes
     * function watcher(acceleration){
     *   alert("The acceleration has changed");
     * }
     *
     * // registers to be notified when acceleration changes
     * // (minimum time between notifications is 10 secs)
     * deviceapis.accelerometer.watchAcceleration(watcher,
     *       function(error){alert("An error occurred");},
     *                 {minNotificationInterval:10000}  )
     */

    /**
     * <p>통지 이벤트 사이의 최소 간격. 이 값이 클수록 통지 이벤트의 발생 빈도가 줄어듭니다. watchAcceleration 함수 호출로 발생하는 통지 이벤트들 사이의 최소 간격을 milliseconds단위로 지정합니다.</p>
     * @type long
     * @memberOf AccelerationOptions#
     * @field
     * @name minNotificationInterval
     */

    /**
     * <p>가속도계 센서의 변화를 통지 받는 이벤트 구독을 중지합니다. 이 함수가 호출되면 watchAcceleration함수의 호출로 시작된 가속도계 센서 감시 작업과 이벤트 통지 구독이 중지됩니다. 매개변수 watchId는 이전의 watchAcceleration 함수 호출에서 반환 받은 이벤트 구독 식별자입니다.</p>
     * @param {long} watchId <p>이벤트 구독 식별자</p>
     * @type void
     * @returns {void}
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     * @memberOf Accelerometer#
     *
     * @example
     * // Last acceleration value retrieved
     * var x = 0;
     * var id = 0; // watcher identifier
     *
     * // Receives acceleration changes
     * function watcher(acceleration){
     *   alert("The acceleration in x Axis changed in " +
     *                  acceleration.xAxis - x + " m/s2");
     *   x = acceleration.xAxis;
     * }
     *
     * // Cancel the watch operation
     * function cancelWatch()
     * {
     *    deviceapis.accelerometer.clearWatch(id);
     * }
     *
     * // registers to be notified when acceleration changes
     * // (minimum time between notifications is 10 secs)
     * id = deviceapis.accelerometer.watchAcceleration(watcher,
     *            function(error){alert("An error occurred");},
     *                      {minNotificationInterval:10000}  )
     *
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

//        if (watchID in g_watchIDs) {
//            this.stopWatchWAC('clearWatch', watchID);
//            delete g_watchIDs[watchID];
//        }
    }

    /**
     * <p>지구 중력</p>
     * @constant
     * @name EARTH_GRAVITY
     * @memberOf Accelerometer#
     */


    ////////////////////////////////////////////////////////////////////////////
    // clear watch on unload

    window.addEventListener('unload', function() {
        ax.util.foreach(g_watchIDs, function(watchID, v) {
            try {
                accelerometer.execSyncWAC(CLEAR_WATCH, [ watchID - 0 ]);
                // accelerometer.stopWatchWAC('clearWatch', watchID - 0);
            }
            catch (ignored) {
            }
        });
    }, false);

    // ====================================================
    Accelerometer.prototype = ax.plugin('deviceapis.accelerometer', {
        'getCurrentAcceleration': getCurrentAcceleration,
        'watchAcceleration': watchAcceleration,
        'clearWatch': clearWatch
    });
    ax.def(Accelerometer.prototype).constant('EARTH_GRAVITY', -9.8);
    ax.def(Accelerometer).constant('EARTH_GRAVITY', -9.8);
    ax.def(g).constant('Accelerometer', Accelerometer);
    ax.def(deviceapis).constant('accelerometer', new Accelerometer());
    // ====================================================
}(window));

/**
 * 가속도계의 성공 콜백 함수를 정의합니다.
 * <p>이 콜백 인터페이스는 매개변수로 Acceleration객체를 전달 받는 성공 콜백 함수를 정의합니다. 이 콜백 함수는 getCurrentAcceleration과 watchAcceleration 함수에서 비동기 연산의 결과를 처리하기 위해 사용됩니다.</p>
 * @namespace
 * @name AccelerationSuccessCallback
 */

/**
 * <p>
 * 비동기 호출이 성공한 경우 호출됩니다.
 * </p>
 * @function
 * @name onsuccess
 * @param {Acceleration} acceleration 단말의 가속도
 * @return {void}
 * @memberOf AccelerationSuccessCallback
 */
