/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview iOS Extension API
 * @version 1.0
 */
(function(g) {
    "use strict";
    var _DEBUG = !!g._APPSPRESSO_DEBUG;
    //-------------------------------------------------------------

    var PLUGIN_ID = 'ax.ext.ios';
    var PLUGIN_NS = 'ax.ext.ios';

    /**
     * iOS Extension API - iOS 전용 디바이스 확장 API
     *
     * @namespace
     * @name ax.ext.ios
     */

    /**
     * finish this app.
     * 앱 종료.
     *
     * @param {number} exitCode (optional; default:0)
     * @methodOf ax.ext.ios
     *
     * @example
     * ax.ext.ios.finish();
     */
    function finish(exitCode) {
        return this.execAsync('finish', ax.nop, ax.nop, [ exitCode || 0 ]);
    }

    /**
     * generate uuid - 새로운 uuid를 리턴.
     * ios 5.0 이후 udid는 deprecate 되었습니다.
     * 애플에서는 udid 대신 uuid를 생성하여 사용하길 권장하고 있으며,
     * getUUID 호출 시 마다 새로 생성되므로 uuid를 단말의 unique id 로 사용하려면 최초 생성 후 로컬 스토리지 등에 저장 하세요.
     *
     * @param {function} callback UUID를 전달 받을 callback
     * @methodOf ax.ext.ios
     *
     * @example
     * var callback = function(uuid){
     *   console.log(uuid);
     * };
     * ax.ext.ios.getUUID(callback);
     */
    function getUUID(callback) {
        return this.execAsync('getUUID',callback, ax.nop, [ ]);
    }

    //-------------------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'finish': finish,
    'getUUID' : getUUID
    }, PLUGIN_NS);
}(window));
