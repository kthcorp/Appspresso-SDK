/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Android Extension API
 * @version 1.0
 */
(function(g) {
    "use strict";
    var _DEBUG = !!g._APPSPRESSO_DEBUG;
    //------------------------------------------------

    var PLUGIN_ID = 'ax.ext.admob';
    var PLUGIN_NS = 'ax.ext.admob';

    /**
     * Admob Extension API - Admob 광고 제어.<br>
     * http://www.admob.com/ 에서 발급 받은 "Publisher ID"가 필요.<br>
     * <em>※ 앱스프레소 1.0 에서는 iOS만 지원</em><br>
     *
     * @namespace
     * @name ax.ext.admob
     */

    /**
     * showAdmob 호출 시 옵션 파라미터로 사용될 객체. 광고 크기 및 화면상의 위치 지정을 위한 옵션.
     *
     * @class
     * @name ShowAdmobOpts
     * @memberOf ax.ext.admob
     * @property {number} position 광고 위치 - position이 지정된 경우 top/left/width/height 옵션은 무시 됨. <br/>
     * 1: top-left, 2: top-center, 3: top-right, 4: bottom-left, 5: bottom-center, 6: bottom-right
     * @property {number} top 상단 여백
     * @property {number} left 좌측 여백
     * @property {number} width 넓이
     * @property {number} height 높이
     * @memberOf ax.ext.admob
     * @see ax.ext.admob.showAdmob
     */

    /**
     * Admob 배너를 생성하고 보여 줌.
     *
     * @param {string} pubId
     * @param {ax.ext.admob.ShowAdmobOpts} opts 광고 크기 및 화면상의 위치 지정을 위한 옵션 (optional; default:bottom of screen)
     * @methodOf ax.ext.admob
     *
     * @example
     * // default position (bottom of screen)
     * var pubID = 'a14eba45e8a8d39';
     * ax.ext.admob.showAdmob(pubID);
     *
     * // 상단 200px 부터 광고 출력
     * ax.ext.admob.showAdmob(pubID, {'top': 200});
     */
    function showAdmob(pubId, opts) {
        this.execAsync('showAdmob', ax.nop, ax.nop, [ pubId || '', opts || {} ]);
    }

    /**
     * Admob 베너 제거.
     *
     * @param {string} pubId
     * @methodOf ax.ext.admob
     *
     * @example
     * var pubID = 'a14eba45e8a8d39';
     * ax.ext.admob.hideAdmob(pubID);
     */
    function hideAdmob(pubId) {
        this.execAsync('hideAdmob', ax.nop, ax.nop, [ pubId || '' ]);
    }

    /**
     * Admob 배너를 새로고침 함.
     *
     * @param {string} pubId
     * @methodOf ax.ext.admob
     *
     * @example
     * var pubID = 'a14eba45e8a8d39';
     * ax.ext.admob.refreshAdmob(pubID);
     */
    function refreshAdmob(pubId) {
        this.execAsync('refreshAdmob', ax.nop, ax.nop, [ pubId || '' ]);
    }

    //------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'showAdmob': showAdmob,
        'hideAdmob': hideAdmob,
        'refreshAdmob': refreshAdmob
    }, PLUGIN_NS);
}(window));
