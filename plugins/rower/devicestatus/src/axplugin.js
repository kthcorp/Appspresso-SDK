////////////////////////////////////////////////////////////////////////////////
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Devicestatus Module. <p>단말의 상태 정보는 카테고리로 그룹화된 속성의 계층구조로 구성됩니다. 이 인터페이스는 그룹화된 속성의 트리를 탐색하며 각 속성의 현재 상태 값을 얻을 수 있고 특정 속성에 대해 값이 변경되면 이를 비동기적으로 통지해 주는 함수를 제공합니다.
 * 트리의 정보는 WAC에 의해 규정된 어휘집(vocabulary)으로 구성됩니다.<br><br>
 * • <b>Aspects</b> : 어휘집은 ’WebRuntime’, ’Display’, ‘MemoryUnit’ 등과 같이 단말의 고유한 특성 정보를 수집할 수 있는 여러 종류의 apsect로 구성됩니다.<br>
 * • <b>Component</b> : 단말에는 aspect의 실체로서 여러 종류의 component가 존재할 수 있습니다. 예를 들면 ‘Memory’ aspect에는 ’physical’, ‘virtual’, ‘storage’와 같이 세 종류의 component가 존재할 수 있습니다. component에는 ‘_default’, ‘_active’ 두 종류의 특별한 식별자가 있습니다. ‘_default’ component는 aspect의 component들 중 기본 component를 지칭하며 ‘_active’ component는 현재 활성화된 component를 지칭합니다.<br>
 * • <b>Properties</b> : Aspect는 component의 특정 속성에 대한 정보를 제공하는 property들로 구성됩니다. 예를 들어 ‘MemoryUnit’에 해당하는 모든 component들은 ‘size’, ‘availableSize’, ‘removable’ 등의 property가 지원됩니다.<br><br>
 *
 * 개별 property의 값을 얻고자 할 때 aspect와 property의 식별자는 반드시 지정해야 합니다. 그 외에 다른 매개변수를 지정하지 않은 경우 우선 활성화된 component를 대상으로 하고 활성화된 component가 없는 경우에는 기본 component를 대상으로 합니다.<br>
 * 아래는 WAC의 어휘집과 Appspresso 1.1에서 Android 런타임과 iOS런타임의 지원현황입니다.<br>
 * <table cellSpacing="1" cellPadding="1" width="100%" border="1" align='center'>
 * <tbody>
 *     <tr align='center'>
 *         <td style="background:#efefef">Aspect</td>
 *         <td style="background:#efefef">Property</td>
 *         <td style="background:#efefef">Android 런타임</td>
 *         <td style="background:#efefef">iOS 런타임</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan = '2'>Battery</td>
 *         <td>batteryLevel</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>batteryBeingCharged</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>CellularHardware</td>
 *         <td>status</td>
 *         <td align='center'>지원</td>
 *         <td><center>지원</center>단 WiFi 네트워크에 연결되어 있는 경우에는 항상 false를 반환합니다.</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan='5'>CellularNetwork</td>
 *         <td>isInRoaming</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>signalStrength</td>
 *         <td><center>지원</center>단 명세에서 정의한 1에서 100 사이의 값이 아닌 단말에서 얻은 값을 그대로 반환합니다.</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>operatorName</td>
 *         <td><center>지원</center>단 WCDMA가 아닌 CDMA 단말은 지원되지 않습니다.</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>mcc</td>
 *         <td>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>mnc</td>
 *         <td>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan='4'>Device</td>
 *         <td>imei</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>model</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>version</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>vendor</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan='6'>Display</td>
 *         <td>resolutionHeight</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>resolutionWidth</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>dpiX</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>dpiY</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>pixelAspectRatio</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>colorDepth</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan='3'>MemoryUnit</td>
 *         <td>size</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>removable</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>availableSize</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan='4'>Operating System</td>
 *         <td>language</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>version</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>name</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>vendor</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan='5'>WebRuntime</td>
 *         <td>wacVersion</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>supportedImageFormats</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>version</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>name</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>vendor</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td>WiFiHardware</td>
 *         <td>status</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan='3'>WiFiNetwork</td>
 *         <td>ssid</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>signalStrength</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>미지원</td>
 *     </tr>
 *     <tr>
 *         <td>networkStatus</td>
 *         <td align='center'>지원</td>
 *         <td align='center'>지원</td>
 *     </tr>
 * </tbody>
 * </table>
 * </p>
 * <p><br>
 * http://wacapps.net/api/devicestatus 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 DeviceStatusManager 인터페이스의 인스턴스가 deviceapis. devicestatus로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/devicestatus</strong><br>
 * 모든 기능을 사용할 수 있습니다.<br>
 * <strong>http://wacapps.net/api/devicestatus.deviceinfo</strong><br>
 * Aspect가 다음 중 하나일 때 getPropertyValue, watchPropertyValue 함수를 사용할 수 있습니다.<br>
 * • Battery<br>
 * • Device<br>
 * • Display<br>
 * • MemoryUnit<br>
 * • OperatingSystem<br>
 * • WebRuntime<br>
 * <strong>http://wacapps.net/api/devicestatus.networkinfo</strong><br>
 * Aspect가 다음 중 하나일 때 getPropertyValue, watchPropertyValue 함수를 사용할 수 있습니다.<br>
 * • CellularHardware<br>
 * • CellularNetwork<br>
 * • WiFiHardware<br>
 * • WiFiNetwork<br><br>
 * ※ Appspresso는 WAC 위젯 외에도 Android와 iOS 앱을 개발할 수 있도록 하기 위해 config.xml 대신 플랫폼 중립적인 메타 정보를 정의할 수 있도록 project.xml 파일을 제공합니다. Appspresso에서 개발한 앱을 WAC 위젯으로 내보낼 때 Appspresso SDK는 project.xml 파일을 기초로 config.xml 파일을 자동 생성하여 WAC 위젯에 포함시킵니다. 이 문서에서는 WAC의 Waikiki API를 설명하기 위해 config.xml에 대한 설명을 그대로 옮깁니다.<br><br>
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

    var g_watchID = 0;
    var g_watchIDs = {};
    var INTERVAL_MIN = 50;
    var INTERVAL_DEFAULT = 30000;

    function castAsString(value) {
        return (value === null || value === undefined) ? value : value + '';
    }


    /**
     * <p>이 인터페이스는 property 식별자를 지정하는 property 속성, component 식별자를 지정하는 component 속성, aspect 식별자를 지정하는 aspect 속성으로 구성되어 하나의 속성을 식별합니다. property와 aspect 속성은 반드시 지정해야 하며 component 속성은 생략 가능합니다.</p>
     * @class
     * @name PropertyRef
     * @property {DOMString} component Component 식별자 (생략 가능)
     * @property {DOMString} aspect Aspect 식별자 (필수)
     * @property {DOMString} property property 식별자 (필수)
     */
    function revisePropertyRef(prop) {
        return {
            aspect: castAsString(prop.aspect),
            property: castAsString(prop.property),
            component: castAsString(prop.component ? prop.component : "_default")
        };
    }

    function validatePropertyRef(prop) {
        ax.util.validateRequiredObjectParam(prop);
        ax.util.validateRequiredStringParam(prop.aspect);
        ax.util.validateRequiredStringParam(prop.property);
        ax.util.validateOptionalStringParam(prop.component);
    }

    function validateReviseWatchOption(options) {
        var intvMin = 0, intvMax = 0, percentMin = 0;

        ax.util.validateOptionalObjectParam(options);
        if (options) {
            ax.util.validateOptionalNumberParam(options.minNotificationInterval);
            ax.util.validateOptionalNumberParam(options.maxNotificationInterval);
            ax.util.validateOptionalNumberParam(options.minChangePercent);

            if (options.maxNotificationInterval && options.maxNotificationInterval < options.minNotificationInterval) {
                throw new DeviceAPIError(ax.INVALID_VALUES_ERR, 'maxNotificationInterval must be greater than minNotificationInterval');
            }

            intvMin = options.minNotificationInterval ? options.minNotificationInterval - 0 : 0;
            intvMax = options.maxNotificationInterval ? options.maxNotificationInterval - 0 : 0;
            percentMin = options.minChangePercent ? options.minChangePercent - 0 : 0;
        }

        return {
            minNotificationInterval: intvMin,
            maxNotificationInterval: intvMax,
            minChangePercent: percentMin
        };
    }

    function validateAspect(aspect) {
        ax.util.validateRequiredStringParam(aspect);
        return castAsString(aspect);
    }

    function validateProperty(property) {
        ax.util.validateOptionalStringParam(property);
        return castAsString(property);
    }

    ////////////////////////////////////////////////////////////////////////////
    // interface Deviceapis

    /**
     * <p>단말의 상태 정보를 질의하기 위한 인터페이스입니다. 이 인터페이스는 단말의 현재 상태에 대한 값을 얻거나 상태의 변화를 지속적으로 통지 받기 위한 이벤트 구독을 제공합니다. 이 인터페이스는 DeviceStatusManager 인터페이스에 의해 deviceapis 객체의 속성으로 제공됩니다.</p>
     * @class 단말의 상태 정보를 질의하기 위한 인터페이스입니다.
     * @name DeviceStatusManager
     * @example
     * function onValueRetrieved(value) {
     *   alert("The battery level is " + value);
     * }
     *
     * deviceapis.devicestatus.getPropertyValue(
     *  onValueRetrieved,
     *  function(e){alert("An error occurred " + e.message);},
     *  {property:"batteryLevel", aspect:"Battery"});
     */
    function DeviceStatusManager() {
    }

    ////////////////////////////////////////////////////////////////////////////
    // devicestatus.getComponents()

    var GET_COMPONENTS = 'getComponents';

    /**
     * <p>특정 aspect에 대해 모든 component들의 식별자를 반환합니다. 매개변수로 지정한 aspect에 해당하는 모든 component들의 식별자 목록을 제공합니다. WAC 어휘집에 정의되지 않은 aspect 식별자나 유효하지 않은 aspect 식별자를 지정한 경우에는 null을 반환합니다.</p>
     * @param {DOMString} aspect <p>대상 aspect의 식별자.</p>
     * @memberOf DeviceStatusManager#
     * @type StringArray
     * @returns {StringArray} <p>component들의 식별자 목록.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     * @example
     * var displays = devicestatus.getComponents('Display');
     */
    function getComponents(aspect) {
        aspect = validateAspect(aspect);
        var result = this.execSyncWAC(GET_COMPONENTS, [aspect]);
        return (result === undefined || result === null) ? null : result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // devicestatus.isSupported()

    var IS_SUPPORTED = 'isSupported';

    /**
     * <p>특정 property에 대해 지원 여부를 확인합니다.매개변수로 지정한 aspect와 property에 대해 지원되는 경우 true, 지원되지 않는 경우 false를 반환합니다. 만약 property 매개변수를 생략하거나 null로 지정한 경우 aspect에 대해서만 지원 여부를 확인합니다. 이 경우 aspect의 property들 중 하나라도 지원한다면 true, 반대의 경우에는 false를 반환합니다.<br>
     * 비록 WAC의 어휘집에 정의되어 있더라도 대상 단말과 대상 플랫폼에 따른 제약으로 인해 상태 정보를 얻을 수 없는 property도 있을 수 있습니다. 이러한 property들에 대해 getPropertyValue 함수를 호출한 경우에는 NOT_AVAILABLE_ERR를 반환합니다.
     * </p>
     *
     * @param {DOMString} aspect <p>대상 aspect의 식별자</p>
     * @param {DOMString} property <p>대상 property 식별자</p>
     * @memberOf DeviceStatusManager#
     * @type boolean
     * @returns {boolean} <p>대상 property와 aspect가 지원되는 경우 true를 지원되지 않는 경우 false를 반환합니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * function onValueRetrieved(value) {
     *   alert("The battery level is " + value);
     * }
     *
     * if (deviceapis.devicestatus.isSupported('Battery','batteryLevel')) {
     *   deviceapis.devicestatus.getPropertyValue(
     *     onValueRetrieved,
     *     function(error){alert("An error occurred " + error.message),
     *     {property:"batteryLevel", aspect:"Battery"});
     *   );
     * }
     */
    function isSupported(aspect, property) {
        aspect = validateAspect(aspect);
        property = validateProperty(property);
        return this.execSyncWAC(IS_SUPPORTED, [aspect, property]);
    }

    ////////////////////////////////////////////////////////////////////////////
    // devicestatus.getPropertyValue()

    var GET_PROPERTY_VALUE = 'getPropertyValue';

    /**
     * <p>지정된 property의 값을 반환합니다. 이 함수는 지정된 property에 대해 현재의 상태 값을 반환합니다. 매개변수 prop 객체의 속성 중 property와 aspect는 반드시 지정해야 하지만 component 속성은 생략 가능합니다. component 속성을 생략한 경우 활성 component(_active)를 우선 대상으로 하며 활성 component가 없다면 기본 component(_default)를 대상으로 합니다.<br>
     * 이 함수는 호출되면 즉시 제어권을 반환하고 비동기적으로 현재의 상태 값을 조사합니다. 상태 값을 조사하는 데 성공한 경우에는 속성 값을 매개변수로 successCallback 함수를 호출합니다<br>
     * 상태 값 조사에 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * • NOT_SUPPORTED_ERR:이 피쳐가 지원되지 않는 경우<br>
     * • SECURITY_ERR:이 연산이 허용되지 않는 경우.<br>
     * • NOT_FOUND_ERR:유효하지 않은 property 식별자를 지정한 경우 (예를 들어 WAC의 어휘집에 정의되지 않은 property)<br>
     * • NOT_AVAILABLE_ERR: 유효한 property 식별자를 지정했으나 현재의 상태 값을 알 수 없는 경우<br>
     * • UNKNOWN_ERR:그 밖에 다른 모든 경우
     * </p>
     *
     * @param {PropertyValueSuccessCallback} successCallback <p>상태 값을 얻는데 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallBack} errorCallback <p>상태 값을 얻는데 실패한 경우 콜백으로 호출됩니다.</p>
     * @param {PropertyRef} prop <p>대상이 되는 특정 속성에 대한 참조.</p>
     * @memberOf DeviceStatusManager#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * function onValueRetrieved(value){
     *   alert("The battery level is " + value);
     * }
     *
     * deviceapis.devicestatus.getPropertyValue(
     *  onValueRetrieved,
     *  function(e){alert("An error occurred " + e.message);},
     *  {property:"batteryLevel", aspect:"Battery"});
     *
     */
    function getPropertyValue(successCallback, errorCallback, prop) {
        validatePropertyRef(prop);
        prop = revisePropertyRef(prop);
        return this.execAsyncWAC("getPropertyValue", function(value) {
            successCallback(value, prop);
        }, errorCallback, [prop]);
    }

    ////////////////////////////////////////////////////////////////////////////
    // devicestatus.watchPropertyChange()

    var WATCH_PROPERTY_CHANGE = 'watchPropertyChange';

    function getOptions(oldOptions) {
        var newOptions = {
            minNotificationInterval: INTERVAL_MIN,
            maxNotificationInterval: Number.MAX_VALUE,
            minChangePercent: 0
        };

        if (oldOptions.minNotificationInterval && oldOptions.minNotificationInterval > INTERVAL_MIN) {
            newOptions.minNotificationInterval = oldOptions.minNotificationInterval;
        }

        if (oldOptions.maxNotificationInterval) {
            if (oldOptions.maxNotificationInterval > newOptions.minNotificationInterval) {
                newOptions.maxNotificationInterval = oldOptions.maxNotificationInterval;
            } else {
                newOptions.maxNotificationInterval = newOptions.minNotificationInterval + 20;
            }
        }

        if (oldOptions.minChangePercent) {
            newOptions.minChangePercent = oldOptions.minChangePercent;
        }

        return newOptions;
    }

    function runPolling(self, pollingID, scb, ecb, prop, options) {
        (function() {
            var oldValue, lastFireTime;
            var _pollingID = pollingID, _scb = scb, _ecb = ecb, _prop = prop;
            var _options = getOptions(options);
            var percentMin = _options.minChangePercent;
            var first = true;

            //TODO: Android와 iOS 리턴포맺 상이. 확인필요 -  althjs
            // 안드로이드 리턴:    {"id":12,"error":null,"result":[73,{"property":"batteryLevel","aspect":"Battery"}]}
            // iOS리턴:         {"id":12,"error":null,"result":100}
            function fireSuccessCallback(value, time) {
                oldValue = value;
                lastFireTime = time;
                ax.util.invokeLater(null, _scb, value, prop);
            }

            g_watchIDs[_pollingID] = window.setInterval(function() {
                var newValue, now, elapsed, _self = self;

                try {
                    newValue = _self.execSyncWAC(GET_PROPERTY_VALUE, [_prop, _pollingID, first]);
                    first = false;
                }
                catch (e) {
                    window.clearInterval(_pollingID);
                    ax.util.invokeLater(null, _ecb, e);
                    return;
                }

                now = Date.now();
                elapsed = now - lastFireTime;

                // 최초 한 번, max interval 초과한 경우 무조건 알려준다
                if (oldValue === undefined || _options.maxNotificationInterval < elapsed) {
                    fireSuccessCallback(newValue, now);
                }
                 else {
                 // 값이 변한 경우에만 알려준다. 단 min percent를 지정한 경우 이를 초과할 경우에만 알려준다
                     if (percentMin && typeof newValue == 'number') {
                        var delta = percentMin ? oldValue * (percentMin / 100) : 0;

                        if ((oldValue + delta) < newValue || newValue < (oldValue - delta)) {
                            fireSuccessCallback(newValue, now);
                        }
                     }
                     else {
                         if (oldValue != newValue) {
                             fireSuccessCallback(newValue, now);
                         }
                     }
                 }
            },  _options.minNotificationInterval);
        })();
    }



    /**
     * <p>상태 값의 변화를 지속적으로 통지 받기 위해 이벤트 구독을 시작합니다. 매개변수 prop 객체의 속성 중 property와 aspect는 반드시 지정해야 하지만 component 속성은 생략 가능합니다. component 속성을 생략한 경우 활성 component(_active)를 우선 대상으로 하며 활성 component가 없다면 기본 component(_default)를 대상으로 합니다.
     * 이 함수가 호출되면 즉시 이벤트 구독 식별자를 반환한 후에 지정한 속성의 상태 변화를 감시하는 작업을 비동기적으로 시작합니다. 상태 변화를 감시하는 작업이 시작되면 지속적으로 상태 값을 포함하는 PropertyRef 객체를 매개변수로 하여 successCallback 함수가 호출됩니다. 상태 값을 감시하는 작업과 통지 이벤트 구독을 중지하려면 반환 받은 이벤트 구독 식별자를 매개변수로 하여 clearWatch 함수를 호출해야 합니다.
     * options 매개변수를 이용하여 통지 이벤트의 발생 조건을 제어할 수 있습니다. 이 매개변수는 생략 가능합니다.
     * 만약 상태 값의 변화를 감시하는 작업이 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수가 호출됩니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * •NOT_SUPPORTED_ERR:이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR:이 연산이 허용되지 않는 경우<br>
     * •NOT_FOUND_ERR:유효하지 않은 property 식별자를 지정한 경우 (예를 들어 WAC의 어휘집에 정의되지 않은 property)<br>
     * •NOT_AVAILABLE_ERR:유효한 property 식별자를 지정했으나 현재의 상태 값을 알 수 없는 경우<br>
     * •UNKNOWN_ERR:그 밖에 다른 모든 경우
     * </p>
     *
     * @param {PropertyValueSuccessCallback} successCallback <p>속성 값의 변화를 알리기 위해 콜백으로 호출됩니다</p>
     * @param {ErrorCallBack} errorCallback <p>오류가 발생하면 콜백으로 호출됩니다.</p>
     * @param {PropertyRef} prop <p>대상이 되는 특정 속성에 대한 참조.</p>
     * @param {WatchOptions} options <p>통지 이벤트의 발생 조건에 대한 선택사항들을 속성으로 제공하는 객체</p>
     * @memberOf DeviceStatusManager#
     * @type long
     * @returns {long} <p>이벤트 구독 식별자</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * function propertyChange(value, ref) {
     *   alert("New value for " + ref.property + " is " + value);
     * }
     *
     * deviceapis.devicestatus.watchPropertyChange(propertyChange, null
     *                        {property:"batteryLevel", aspect:"Battery"});
     *
     */
    function watchPropertyChange(successCallback, errorCallback, prop, options) {
        ax.util.validateRequiredFunctionParam(successCallback);
        ax.util.validateOptionalFunctionParam(errorCallback);

        var watchID;

        validatePropertyRef(prop);
        prop = revisePropertyRef(prop);
        options = validateReviseWatchOption(options);
        watchID = g_watchID++;

        //TODO: runPolling 안에서 setInterval 을 이용해 errorAsyncWAC 을 쓸수 있는지 모르겠음. 당장은 임시 에러콜백 생성 ㅠㅠ - althjs
        if( errorCallback == null || errorCallback == undefined){
            errorCallback = function(error){
                ax.console.warn('error callback was ignored: error=' + error);
            };
        }

        runPolling(this, watchID, successCallback, errorCallback, prop, options || {});

        return watchID;

        // function scb(result) {
        //     ax.util.invokeLater(null, successCallback, result[0], result[1]);
        // }
        //
        // watchID = this.watchWAC(WATCH_PROPERTY_CHANGE, scb, errorCallback, [prop, options]);
        // g_watchIDs[watchID] = null;
        //
        // return watchID;
    }

    ////////////////////////////////////////////////////////////////////////////
    // devicestatus.clearPropertyChange()

    var CLEAR_PROPERTY_CHANGE = 'clearPropertyChange';



    /**
     * <p>상태 값의 변화에 대한 통지 이벤트 구독을 중지합니다. 이 함수가 호출되면 watchPropertyChange 함수의 호출로 시작된 상태 변화를 감시하는 작업과 이벤트 통지 구독이 중지됩니다. 매개변수 watchHandler는 watchPropertyChange 함수 호출에서 반환 받은 이벤트 구독 식별자입니다.</p>
     *
     * @param {long} watchHandler <p>이벤트 구독 식별자</p>
     * @memberOf DeviceStatusManager#
     * @type void
     * @returns {void}
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * var id = null;
     * function propertyChange(value, ref) {
     *   alert("New value for " + ref.property + " is " + value);
     *   if (id != null) // After receiving the first notification, we clear it
     *     devicestatus.clearPropertyChange(id);
     * }
     *
     * id = deviceapis.devicestatus.watchPropertyChange(propertyChange,
     *                null,{property:"batteryLevel", aspect:"Battery"});
     */
    function clearPropertyChange(watchHandler) {
        ax.util.validateRequiredNumberParam(watchHandler);

        if (!(watchHandler in g_watchIDs)) {
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR, 'invalid watchHandler');
        }

        window.clearInterval(g_watchIDs[watchHandler]);
        delete g_watchIDs[watchHandler];
        this.execSyncWAC(CLEAR_PROPERTY_CHANGE, [watchHandler - 0]);

        // ax.util.validateParamWAC(watchHandler, true, false, 'number', true, 'watchHandler');
        // this.stopWatch(CLEAR_PROPERTY_CHANGE, watchHandler);
    }

    ////////////////////////////////////////////////////////////////////////////
    // clear watch on unload

    window.addEventListener('unload', function() {
        for (var watchID in g_watchIDs) {
            try {
                devicestatus.stopWatchWAC(CLEAR_PROPERTY_CHANGE, [ watchID - 0 ]);
            }
            catch (ignored) {
            }
        }
    }, false);

    // ====================================================
    DeviceStatusManager.prototype = ax.plugin('deviceapis.devicestatus', {
        'getComponents': getComponents,
        'isSupported': isSupported,
        'getPropertyValue': getPropertyValue,
        'watchPropertyChange': watchPropertyChange,
        'clearPropertyChange': clearPropertyChange
    });
    ax.def(g).constant('DeviceStatusManager', DeviceStatusManager);
    ax.def(deviceapis).constant('devicestatus', new DeviceStatusManager());
    // ====================================================
}(window));

/**
 * <p>watchPropertyChange 함수에서 사용되는 선택사항입니다.</p>
 *
 * @class watchPropertyChange 함수에서 사용되는 선택사항입니다.
 * @name WatchOptions
 * @property {long} maxNotificationInterval <p>통지 이벤트 사이의 최소 시간 간격으로 milliseconds 단위입니다. 이 값이 클수록 통지 이벤트의 발생 빈도가 줄어듭니다. 마지막 통지 이벤트가 발생 한 후 최소한 이 속성에서 지정한 시간이 초과된 후에 다음 통지 이벤트가 발생합니다. 만약 maxNotificationInterval의 값이 0보다 크다면 이 값은 그 보다 작아야 합니다. 그렇지 않은 경우 이 값은 올바르지 않은 값으로 간주됩니다. 이 값이 null이거나 0(기본값)인 경우 이 매개변수는 무시됩니다.</p>
 * @property {long} maxNotificationInterval <p>통지 이벤트 사이의 최대 시간 간격으로 milliseconds 단위입니다. 마지막 통지 이벤트가 발생한 후 상태 값에 변화가 없더라도 이 속성에서 지정한 시간이 경과하면 통지 이벤트가 발생합니다. 만약 minNotificationInterval의 값이 0보다 크다면 이 값은 그 보다 커야 합니다. 그렇지 않은 경우 이 값은 올바르지 않은 값으로 간주됩니다. 이 값이 null이거나 0(기본값)인 경우 이 매개변수는 무시됩니다.</p>
 * @property {long} minChangePercent <p>값의 최소 변화량입니다. 이 속성은 상태 값이 숫자 형식인 경우에만 유효하며 마지막 통지 이벤트에서 전달 받은 상태 값 대비 최소의 변화량을 백분율로 지정합니다. 예를 들어 마지막으로 통지된 상태 값이 100이고 이 속성에 지정한 최소 변화량의 값이 20(%)이면 상태 값이 80 미만이 되거나 120을 초과할 때 다음 통지 이벤트가 발생합니다.<p>
 * <p>만약 minChangePercent 속성과 minNotificationInterval 속성을 동시에 지정하거나 minChangePercent 속성과 maxNotificationInterval 속성을 동시에 지정한 경우 minNotificationInterval 속성과 maxNotificationInterval 속성이 우선 적용됩니다.</p>
 *
 * @example
 * function propertyChange(value, ref) {
 *   alert("New value for " + ref.property + " is " + value);
 * }
 *
 * // Subscribe for being notified when battery level changes,
 * invocations to the listener will be at least separated by 60 secs
 * deviceapis.devicestatus.watchPropertyChange(propertyChange,null,
 *                     {property:"batteryLevel", aspect:"Battery"},
 *                     {minNotificationInterval:60000});
 *
 */

/**
 * <p>상태 값을 얻기 위한 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name PropertyValueSuccessCallback
 */

/**
 * <p>상태 값을 얻는 데 성공한 경우 호출됩니다.</p>
 * @function
 * @memberOf PropertyValueSuccessCallback
 * @name onsuccess
 * @param {Object} value <p>대상 속성의 상태 값</p>
 * @param {PropertyRef} property <p>대상이 되는 특정 속성에 대한 참조.</p>
 * @return {void}
 */
