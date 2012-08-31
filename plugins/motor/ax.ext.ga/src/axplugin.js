/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Google Analytics Extension API
 * @version 1.0
 */
(function(g) {
    "use strict";
    var _DEBUG = !!g._APPSPRESSO_DEBUG;
    //-------------------------------------------------------------

    var PLUGIN_ID = 'ax.ext.ga';
    var PLUGIN_NS = 'ax.ext.ga';

    /**
     * Google Analytics Extension API.
     * 구글 웹로그 분석을 이용하려면 http://www.google.com/analytics/ 에서 사이트 등록 후 accountId를 발급 받아야 함.
     *
     * @namespace
     * @name ax.ext.ga
     */

    /**
     * start ga tracker.
     * 웹로그 분석 추적 시작
     *
     * @param {string} accountId 구글 Analytics 아이디
     * @param {number} dispatchPeriod in sec. (optional; default:10)
     * @methodOf ax.ext.ga
     */
    function startTracker(accountId, dispatchPeriod) {
        this.execAsync('startTracker', ax.nop, ax.nop, [ accountId, dispatchPeriod || 10 ]);
    }

    /**
     * stop ga tracker.
     * 웹로그 분석 추적 종료.
     *
     * @methodOf ax.ext.ga
     */
    function stopTracker() {
        this.execAsync('stopTracker', ax.nop, ax.nop, [  ]);
    }

    /**
     * track event.
     * 이벤트 추적. http://code.google.com/apis/analytics/docs/gaJS/gaJSApiEventTracking.html
     *
     * @param {string} category
     * @param {string} action
     * @param {string} label
     * @param {number} value (optional; default:-1)
     * @methodOf ax.ext.ga
     */
    function trackEvent(category, action, label, value) {
        this.execAsync('trackEvent', ax.nop, ax.nop, [ category, action || '', label || '', value || -1 ]);
    }

    /**
     * track page view.
     * 페이지뷰 추적.
     *
     * @param {string} page
     * @methodOf ax.ext.ga
     */
    function trackPageview(page) {
        this.execAsync('trackPageview', ax.nop, ax.nop, [ page || '' ]);
    }

    //-------------------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'startTracker': startTracker,
        'stopTracker': stopTracker,
        'trackEvent': trackEvent,
        'trackPageview': trackPageview
    }, PLUGIN_NS);
}(window));
