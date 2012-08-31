/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Deviceinteraction Module. <p>이 모듈은 다음과 같은 사용자와 상호작용할 수 있는 기능을 제공합니다.<br>
 * •단말 진동기 (Device vibrator)<br>
 * •단말 통지기 (Device notifier)<br>
 * •스크린 백라이트 (Screen backlight)<br>
 * •단말 벽지 (Device Wallpaper)<br>
 * </p>
 * <p>
 * http://wacapps.net/api/deviceinteraction 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 DeviceInteractionManager 인터페이스의 인스턴스가 deviceapis.deviceinteraction으로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/deviceinteraction</strong><br>
 * deviceinteraction모듈에 접근합니다.<br><br>
 * ※ Appspresso는 WAC 위젯 외에도 Android와 iOS 앱을 개발할 수 있도록 하기 위해 config.xml 대신 플랫폼 중립적인 메타 정보를 정의할 수 있도록 project.xml 파일을 제공합니다. Appspresso에서 개발한 앱을 WAC 위젯으로 내보낼 때 Appspresso SDK는 project.xml 파일을 기초로 config.xml 파일을 자동 생성하여 WAC 위젯에 포함시킵니다. 이 문서에서는 WAC의 Waikiki API를 설명하기 위해 config.xml에 대한 설명을 그대로 옮깁니다.<br>
 * </p>
 *
 * <p>Appspresso Supporting Status.</p>
 *
 * <table cellspacing="1" cellpadding="1" width="100%" border="1" align="center">
 * <tbody>
 * <tr align="center">
 * <td style="background:#efefef">Interface</td>
 * <td style="background:#efefef">Method</td>
 * <td style="background:#efefef">Android runtime</td>
 * <td style="background:#efefef">iOS runtime</td>
 * </tr>
 * <tr>
 * <td rowspan="7">DeviceInteractionManager</td>
 * <td>startNotify</td>
 * <td align="center">○</td>
 * <td align="center">△</td>
 * </tr>
 * <tr>
 * <td>stopNotify</td>
 * <td align="center">○ </td>
 * <td align="center">○ </td>
 * </tr>
 * <tr>
 * <td> startVibrate </td>
 * <td align="center">○ </td>
 * <td align="center">△ </td>
 * </tr>
 * <tr>
 * <td> stopVibrate </td>
 * <td align="center">○ </td>
 * <td align="center">○ </td>
 * </tr>
 * <tr>
 * <td> lightOn </td>
 * <td align="center">× </td>
 * <td align="center">× </td>
 * </tr>
 * <tr>
 * <td> ightOff </td>
 * <td align="center">× </td>
 * <td align="center">× </td>
 * </tr>
 * <tr>
 * <td> setWallpaper </td>
 * <td align="center">○ </td>
 * <td align="center">× </td>
 * </tr>
 * </tbody></table>
 */

(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');
    var deviceapis = g.deviceapis;// require('deviceapis');

    ////////////////////////////////////////////////////////////////////////////
    // interface DeviceInteractionManager

    var DEVICE_INTERACTION_MANAGER = 'DeviceInteractionManager';
    var DEVICE_INTERACTION = 'deviceinteraction';

    /**
     * <p>DeviceInteractionManager 인터페이스. 이 인터페이스는 모듈의 기능들에 접근을 제공하는 deviceinteraction API의 최상위 인터페이스입니다.</p>
     * @class 모듈의 기능들에 접근을 제공하는 deviceinteraction API의 최상위 인터페이스
     * @name DeviceInteractionManager
     *
     * @example
     * function alertUser()
     * {
     *   // switches the light on for 10 secs
     *   deviceapis.deviceinteraction.lightOn( function() {  return; }, function(e)  { window.console.error(e.code); }, 10000);
     *   // makes the device beep/vibrate/backlight-on for 10 secs
     *   deviceapis.deviceinteraction.startNotify( function() {  return; }, function(e)  { window.console.error(e.code); },10000);
     *   // makes the device vibrate for 10 secs
     *   deviceapis.deviceinteraction.startVibrate( function() {  return; }, function(e)  { window.console.error(e.code); },10000);
     * }
     *
     * function stopAlertingButtonPressed()
     * {
     *   deviceapis.deviceinteraction.lightOff();
     *   deviceapis.deviceinteraction.stopNotify();
     *   deviceapis.deviceinteraction.stopVibrate();
     * }
     */
    function DeviceInteractionManager() {
    }

    // properties/methods of DeviceInteractionManager
    var START_NOTIFY = 'startNotify';
    var STOP_NOTIFY = 'stopNotify';
    var START_VIBRATE = 'startVibrate';
    var STOP_VIBRATE = 'stopVibrate';
    var LIGHT_ON = 'lightOn';
    var LIGHT_OFF = 'lightOff';
    var SET_WALLPAPER = 'setWallpaper';

    /**
     * <p>지정한 시간 동안 비프음을 울립니다. 단말이 무음 모드인 경우에는 진동을 울립니다.이 함수가 호출되면 단말의 볼륨이 0이 아닌 경우에 매개변수 duration에서 지정한 시간 동안 비프음을 울립니다. 볼륨이 0이거나 단말이 무음 모드인 경우에는 비프음 대신에 진동을 울립니다. 지정한 시간이 경과하기 전에 중지시키려면 stopNotify함수를 호출합니다.<br>
     * 만약 이 기능이 지원되지 않는 경우 WAC 웹 런타임은 아무런 행위도 하지 않고 즉각 반환됩니다.</p>
     *
     * @memberOf DeviceInteractionManager#
     * @param {SuccessCallback} successCallback <p>성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallBack} errorCallback <p>오류가 발생한 경우 콜백으로 호출됩니다.</p>
     * @param {long} duration <p>milliseconds단위의 시간</p>
     * @type void
     * @returns {void}
     *
     * @example
     * deviceapis.deviceinteraction.startNotify(
     *                 function() {  return; },
     *                function(e)  { window.console.log('error while notifying the user'); },
     *                10000); // makes the device beep/vibrate/backlight-on for 10 secs
     *
     * function stopNotifierButton() // To be invoked when the user stops the notification manually
     * {
     *   deviceapis.deviceinteraction.stopNotify();
     * }
     */
    function startNotify(successCallback, errorCallback, duration) {
        ax.util.validateRequiredNumberParam(duration);
        return this.execAsyncWAC(START_NOTIFY, successCallback, errorCallback, [Number(duration)]);
    }

    /**
     * <p>비프음(진동)을 중지시킵니다.단말의 비프음(진동)을 중지시킵니다. 만약 비프음이 울리는 중이 아니거나 진동 중이 아니라면 이 호출은 무시됩니다.<br>
     * 만약 이 기능이 지원되지 않는 경우 WAC 웹 런타임은 아무런 행위도 하지 않고 즉각 반환됩니다.</p>
     *
     * @memberOf DeviceInteractionManager#
     * @type void
     * @returns {void}
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * // makes the device beep/vibrate for 10 secs
     * deviceapis.deviceinteraction.startNotify(function() {  return; }, function(e)  { window.console.log( 'Notif not done'); },10000);
     *
     * // To be invoked when the user stops the notification manually
     * function stopNotifierButton()
     * {
     *   deviceapis.deviceinteraction.stopNotify();
     * }
     */
    function stopNotify() {
        this.execSyncWAC(STOP_NOTIFY);
    }

    /**
     * <p>지정한 시간 동안 단말을 진동시킵니다.이 함수가 호출되면 매개변수 duration에서 지정한 시간 동안 단말을 진동시킵니다. 지정한 시간이 지나기 전에 중지시키려면 stopNotify함수를 호출합니다.</p>
     * <p>pattern 매개변수가 입력된 경우 지정된 on/off 패턴에 따라 장치를 진동시킵니다.</p>
     * <p>만약 이 기능이 지원되지 않는 경우 WAC 웹 런타임은 아무런 행위도 하지 않고 즉각 반환됩니다.</p>
     *
     * @param {SuccessCallback} successCallback <p>지정된 경로를 파일 핸들로 변환하는 데 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallBack} errorCallback <p>오류가 발생한 경우 콜백으로 호출됩니다.</p>
     * @param {long} duration  <p>milliseconds단위의 시간</p>
     * @param {DOMString} pattern  <p>진동 패턴.</p>
     * <p>pattern은 ‘.’와 ‘_’문자로 이루어진 문자열입니다. ‘.’은 진동을 지시하고 ‘_’은 진동을 일시중단할 것을 지시합니다. 패턴은 10문자까지만 가능하며 매 진동시간은 100milliseconds입니다.</p>
     * <p>예를 들어 패턴 “..__.”는 장치가 200milliseconds동안 진동하고, 200milliseconds동안 중지하고, 다시 100milliseconds 동안 진동하는 것을 의미합니다.</p>
     *
     * @memberOf DeviceInteractionManager#
     * @type void
     * @returns {void}
     *
     * @example
     * // makes the device vibrate for 10 secs
     * deviceapis.deviceinteraction.startVibrate(function() {  return; }, function(e)  { window.console.error(e.code)},10000);
     *
     * // device should vibrate for 200 msecs, stop vibrating for
     * // another 200 msecs and vibrate again for 100 msecs.
     * deviceapis.deviceinteraction.startVibrate(function() {  return; }, function(e)  { alert(e.code); },null, "..__.");
     *
     * // To be invoked when the user stops the vibration manually
     * function stopVibrationButton()
     * {
     *   deviceapis.deviceinteraction.stopVibrate();
     * }
     */
    function startVibrate(successCallback, errorCallback, duration, pattern) {
        ax.util.validateOptionalStringParam(pattern);

        if (duration !==null && !!duration && !ax.isNumber(duration)) {
            throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR);
        }
        if (!duration && !pattern) { throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR); }
        if (!duration) duration = null;

        if (!pattern) {
            pattern = null;
        } else {
            try {
                validatePattern(pattern);
            } catch(e) {
                this.errorAsyncWAC(e, errorCallback);
                return;
            }
        }

        return this.execAsyncWAC(START_VIBRATE, successCallback, errorCallback, [duration, pattern]);
    }

    function validatePattern(pattern) {
        if(pattern.length == 0) {
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR, 'The pattern length must be longer than 0.');
        }

        if(pattern.length > 10) {
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR, 'The pattern length must be shorter than 11.');
        }

        if(/^[._]([._])*.([._])*$/.test(pattern) == false) {
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR, 'The pattern must be composed by \'.\' and \'_\'.');
        }
    }

    /**
     * <p>진동을 중지시킵니다.단말의 진동을 중지시킵니다. 만약 진동 중이 아니면 이 호출은 무시됩니다.<br>
     * 만약 이 기능이 지원되지 않는 경우 WAC 웹 런타임은 아무런 행위도 하지 않고 즉각 반환됩니다.</p>
     * @memberOf DeviceInteractionManager#
     * @type void
     * @returns {void}
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     * @example
     * // makes the device vibrate for 10 secs
     * deviceapis.deviceinteraction.startVibrate(function() {  return; }, function(e)  { alert(e.code); },10000);
     *
     * // To be invoked when the user stops the vibration manually
     * function stopVibrationButton()
     * {
     *   deviceapis.deviceinteraction.stopVibrate();
     * }
     */
    function stopVibrate() {
        this.execSyncWAC(STOP_VIBRATE);
    }


    /**
     * <p>지정한 시간 동안 백라이트를 켭니다.이 함수가 호출되면 지정한 시간 동안 백라이트가 켜집니다. 지정한 시간이 지나기 전에 중지시키려면 lightOff 함수를 호출합니다.<br>
     * 만약 이 기능이 지원되지 않는 경우 WAC 웹 런타임은 아무런 행위도 하지 않고 즉각 반환됩니다.</p>
     *
     * @memberOf DeviceInteractionManager#
     * @param {SuccessCallback} successCallback <p>지정된 경로를 파일 핸들로 변환하는 데 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallBack} errorCallback <p>오류가 발생한 경우 콜백으로 호출됩니다.</p>
     * @param {long} duration  <p>milliseconds 단위의 값</p>
     * @type void
     * @returns {void}
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * // switches the screen backlight on for 10 secs
     * deviceapis.deviceinteraction.lightOn(function() {  return; }, function(e)  { alert(e.code); },10000);
     *
     * // To be invoked when the user requests to switch off the light manually
     * function noLightButton()
     * {
     *   deviceapis.deviceinteraction.lightOff();
     * }
     */
    function lightOn(successCallback, errorCallback, duration) {
        ax.util.validateRequiredNumberParam(duration);
        return this.execAsyncWAC(LIGHT_ON, successCallback, errorCallback, [duration]);
    }


    /**
     * <p>백라이트를 끕니다.백라이트를 끕니다. 백라이트를 켜 둔 상태가 아니라면 이 호출은 무시됩니다.<br>만약 이 기능이 지원되지 않는 경우 WAC 웹 런타임은 아무런 행위도 하지 않고 즉각 반환됩니다.</p>
     *
     * @memberOf DeviceInteractionManager#
     * @type void
     * @returns {void}
     *
     * @example
     * // switches the light on for 10 secs
     *  deviceapis.deviceinteraction.lightOn(function() {  return; }, function(e)  { alert(e.code); },10000);
     *
     *  // To be invoked when the user requests to switch off the light manually
     *  function noLightButton()
     *  {
     *    deviceapis.deviceinteraction.lightOff();
     *  }
     *
     */
    function lightOff() {
        this.execSyncWAC(LIGHT_OFF);
    }


    /**
     * 단말의 벽지를 설정합니다.
     * <p>매개변수 filename으로 지정한 이미지 파일을 단말의 벽지로 지정합니다. 벽지 지정에 성공한 경우 successCallback 함수가 호출됩니다. 지원되지 않거나 유효하지 않은 이미지 형식 등의 이유로 실패한 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.</p>
     * <ul>
     * <li>NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우</li>
     * <li>SECURITY_ERR: 이 연산이 허용되지 않는 경우</li>
     * <li>UNKNOWN_ERR: 그 밖에 다른 모든 경우</li>
     * </ul>
     *
     * @function
     * @name setWallpaper
     * @memberOf DeviceInteractionManager#
     * @param {SuccessCallback} successCallback <p>설정에 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallBack} errorCallback <p>오류가 발생한 경우 콜백으로 호출됩니다.</p>
     * @param {DOMString} fileName <p>벽지로 설정할 이미지 파일의 가상 경로입니다. 이 경로는 WAC 파일시스템 모듈에서 규정한 형식을 따라야 하며 Filesystem.resolve 함수에서 사용 가능해야 합니다.</p>
     * @type long
     * @returns {long} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * //call async function set wallpaper
     * try{
     *    deviceapis.deviceinteraction.setWallpaper(function() {
     *                           alert("wallpaper set");
     *                         },
     *                         function(e) {
     *                           alert(e.message);
     *                         },
     *                         "images/a.jpg"); // Sets the wallpaper of the device to a.jpg
     *  }
     *  catch(exp) {
     *        alert("setWallpaper Exception :[" + exp.code + "] " + exp.message);
     *  }
     */
    function setWallpaper(successCallback, errorCallback, fileName) {
        ax.util.validateRequiredStringParam(fileName);
        if(!ax.util.isValidPath(fileName)){
            this.errorAsyncWAC(new DeviceAPIError(ax.INVALID_VALUES_ERR), errorCallback);
            return;
        };

        return this.execAsyncWAC(SET_WALLPAPER, successCallback, errorCallback, [fileName]);
    }

    // ====================================================
    DeviceInteractionManager.prototype = ax.plugin('deviceapis.deviceinteraction', {
        'startNotify': startNotify,
        'stopNotify': stopNotify,
        'startVibrate': startVibrate,
        'stopVibrate': stopVibrate,
        'lightOn': lightOn,
        'lightOff': lightOff,
        'setWallpaper': setWallpaper
    });

    ax.def(g).constant(DEVICE_INTERACTION_MANAGER, DeviceInteractionManager);
    ax.def(deviceapis).constant(DEVICE_INTERACTION, new DeviceInteractionManager());
    // ====================================================
}(window));
