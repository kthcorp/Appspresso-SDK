////////////////////////////////////////////////////////////////////////////////
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Pim Module. <p>개인정보관리(PIM) 모듈들은 다음과 같습니다.<br>
 * • contact<br>
 * • task<br>
 * • calendar<br>
 * 각 모듈들에 대한 접근은 deviceapis.pim.contact, deviceapis.pim.task, deviceapis.pim.calendar 인터페이스들을 통해 제공됩니다. 이 명세에서는 이 모듈들에 대한 계층 구조를 정의합니다.<br>
 * </p>
 * <p>
 * http://wacapps.net/api/pim 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 Pim 인터페이스의 인스턴스가 deviceapis pim 으로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 Quick Start Guide를 참고하십시오.<br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * http://wacapps.net/api/pim<br>
 * pim 기본 모듈에 접근합니다.<br>
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

    // ====================================================
    /**
     * <p>Pim 인터페이스에 관련된 속성들이 객체화됩니다. <br>이 인터페이스는 하위 모듈인 contact, calendar, task의 부모 모듈이며 PimObject 인터페이스에 의해 deviceapis 객체의 속성으로 제공됩니다.<br></p>
     * @class Pim 인터페이스에 관련된 속성들이 객체화됩니다.
     * @name Pim
     */
    function Pim() {
    }

    // ====================================================
    ax.def(g).constant('Pim', Pim);
    ax.def(deviceapis).constant('pim', new Pim());
}(window));
