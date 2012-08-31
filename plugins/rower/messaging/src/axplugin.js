////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Messaging Module. <p>messaging API를 통해 SMS, MMS, Email 등을 전송할 수 있습니다. 또한 메시지 폴더를 검색할 수 있습니다.</p>
 * <p>Appspresso의 현재 버전 1.0 beta에서는 오직 SMS 메시지만 생성하고 발송할 수 있습니다.</br>
 * <b>iOS</b>의 경우 개별 어플리케이션이 직접 SMS를 발송하는 것을 허용하지 않으므로 iOS 런타임은 sendMessage 함수를 호출하면 iOS 내장 메시지 어플리케이션을 실행시키고 매개변수로 전달 받은 메시지 객체의 내용을 자동으로 채워줍니다. 메시지가 실제 발송될지 여부는 최종 사용자의 이후 선택에 달려 있습니다.<br>
 * <b>Android</b> 런타임의 경우 직접 SMS를 발송하지만 발송한 내용을 저장하지는 않습니다. 따라서 나중에 발송한 SMS를 검색할 수 없습니다. 이는 Android 단말마다 특성이 다르기 때문입니다. 이후 출시될 Android 단말들에서는 또 어떤 특성이 발견될 지 모르므로 가급적 사용하지 않을 것을 권고합니다.<br>
 * </p>
 * <p>
 * http://wacapps.net/api/messaging 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 Messaging 인터페이스의 인스턴스가 deviceapis.messaging으로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/messaging</strong><br>
 * 모든 기능을 사용할 수 있음.<br>
 * <strong>http://wacapps.net/api/messaging.send</strong><br>
 * Messaging.findMessages(), Message.update(), Messaging.onSMS(), Messaging.onMMS(), Messaging.onEmail()을 제외한 모든 기능 사용<br>
 * <strong>http://wacapps.net/api/messaging.find</strong><br>
 * Messaging.sendMessage(), Message.update(), Messaging.onSMS(), Messaging.onMMS(), Messaging.onEmail()을 제외한 모든 기능 사용<br>
 * <strong>http://wacapps.net/api/messaging.subscribe</strong><br>
 * Messaging.sendMessage(),  Message.findMessages(), Message.update()을 제외한 모든 기능 사용<br>
 * <strong>http://wacapps.net/api/messaging.write</strong><br>
 * Messaging.sendMessage(), Messaging.findMessages(), Messaging.onSMS(), Messaging.onMMS(), Messaging.onEmail()을 제외한 모든 기능 사용<br><br>
 * ※ Appspresso의 현재 버전 1.0에서는 이 피쳐에 의해 FileSystemManager객체와 File객체가 로드되지 않습니다. filesystem 모듈의 관련 피쳐들을 직접 선언해야 합니다.<br>
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

    var TYPE_SMS = 1;
    var TYPE_MMS = 2;
    var TYPE_EMAIL = 3;
    var FOLDER_INBOX = 1;
    var FOLDER_OUTBOX = 2;
    var FOLDER_DRAFTS = 3;
    var FOLDER_SENTBOX = 4;

    var g_watchIDs = {};
    var g_watchID = 30000; // 30000 ~ 39999

    function convertFromDate(filter) {
        var res = {};

        ax.util.foreach(filter, function(key, value) {
            switch(key){
            case 'startTimestamp':
            case 'endTimestamp':
                if (value instanceof Date) {
                    res[key] = value.getTime();
                }
                else {
                    try {
                        res[key] = new Date(value).getTime();
                    }
                    catch (e) {
                        res[key] = -1;
                    }
                }
                break;
            default:
                res[key] = value;
            }
        });

        return res;
    }

    function validateType(type) {
        ax.util.validateRequiredNumberParam(type);

        type -= 0;

        if (type !== TYPE_SMS && type !== TYPE_MMS && type !== TYPE_EMAIL) {
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR);
        }

        if (type !== TYPE_SMS) {
            throw new DeviceAPIError(ax.NOT_SUPPORTED_ERR);
        }

        return type;
    }

    function validateFolder(folder) {
        ax.util.validateRequiredNumberParam(folder);

        if (folder !== FOLDER_INBOX && folder !== FOLDER_OUTBOX && folder !== FOLDER_DRAFTS && folder !== FOLDER_SENTBOX) {
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR);
        }
    }

    function validateMessage(msg) {
        ax.util.validateRequiredObjectParam(msg);
        try {
            ax.util.validateInstance(msg, Message, true, 'contact');
        } catch (e) {
            throw new DeviceAPIError(e);
        }
        ax.util.validateRequiredArrayParam(msg.to);
        ax.util.validateRequiredStringParam(msg.body);
    }

    ///////////////////////////////////////////////////////////////////////
    // interface Message

    /**
     * <p>Messaging creation and sending capabilities.<br>This interface allows a web application to define the set of properties linked to a message previously created through the createMessage() method in the Messaging Interface.</p>
     * @class
     * @name Message
     */
    function Message() {
        var peer = arguments[0];
        peer.timestamp = peer.timestamp ? new Date(peer.timestamp) : undefined;

        var ID = 'id';
        function getId() { return peer.id; }

        var TYPE = 'type';
        function setType(v) {
            validateType(v);
            peer.type = v - 0;
        }
        function getType() { return peer.type; }

        var FOLDER = 'folder';
        function setFolder(v) {
            validateFolder(v);
            peer.folder = v - 0;
        }
        function getFolder() { return peer.folder; }

        var TIMESTAMP = 'timestamp';
        function setTimestamp(v) {
            if (!ax.isObject(v) || !(v instanceof Date)) {
                throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR);
            }

            peer.timestamp = v;
        }
        function getTimestamp() { return peer.timestamp; }

        var FROM = 'from';
        function setFrom(v) {
            ax.util.validateRequiredStringParam(v);
            peer.from = v;
        }
        function getFrom() { return peer.from; }

        var TO = 'to';
        function setTo(v) {
            ax.util.validateRequiredArrayParam(v);
            for (var i = 0; i < v.length; i++) {
                ax.util.validateRequiredStringParam(v[i]);
            }
            peer.to = v;
        }
        function getTo() { return peer.to; }

        var CC = 'cc';
        function setCc(v) {
            ax.util.validateRequiredArrayParam(v);
            peer.cc = v;
        }
        function getCc() { return peer.cc; }

        var BCC = 'bcc';
        function setBcc(v) {
            ax.util.validateRequiredArrayParam(v);
            peer.bcc = v;
        }
        function getBcc() { return peer.bcc; }

        var BODY = 'body';
        function setBody(v) {
            ax.util.validateRequiredStringParam(v);
            peer.body = v;
        }
        function getBody() { return peer.body; }

        var IS_READ = 'isRead';
        function setIsRead(v) {
            ax.util.validateRequiredBooleanParam(v);
            peer.isRead = v;
        }
        function getIsRead() { return peer.isRead; }

        var PRIORITY = 'priority';
        function setPriority(v) {
            ax.util.validateRequiredBooleanParam(v);
            peer.priority = v;
        }
        function getPriority() { return peer.priority; }

        var SUBJECT = 'subject';
        function setSubject(v) {
            ax.util.validateRequiredStringParam(v);
            peer.subject = v + '';
        }
        function getSubject() { return peer.subject; }

        var ATTACHMENT = 'attachment';
        function setAttachment(v) {
            ax.util.validateRequiredArrayParam(v);
            peer.attachment = v;
        }
        function getAttachment() { return peer.attachment; }


        /**
         * <p>Updates a message retrieved with the findMessages method <br> This method is meant to transfer all changes made to the given Message object before (i.e. changed attributes) to the underlying system (e.g. native messaging database and LDAP). If any changes cannot be transferred to the system, they can be ignored by the implementation.<br>
         * Messages in the inbox (deviceapis.messaging.FOLDER_INBOX) outbox (deviceapis.messaging.FOLDER_OUTBOX) and sentbox (deviceapis.messaging.FOLDER_SENTBOX) may only be allowed to change the isRead attribute of the Message object. Messages within the draft folder (deviceapis.messaging.FOLDER_DRAFTS) may also be changed for other attributes as well. However, this is up to the actual implementation and may rely on the underlying system.<br>
         * The implementation has to make sure that an updated Message object is provided in the success callback, which represents the current status of the message. The developer is expected to use this updated message for comparison with the former object to check which fields have or have not been transferred by the implementation.<br>
         * </p>
         *
         * @function
         * @param {UpdateMessageSuccessCallback} successCallback
         * @param {ErrorCallback} errorCallback
         * @memberOf Message#
         * @name update
         * @type PendingOperation
         * @returns {PendingOperation}
         * @exception <p>with error code TYPE_MISMATCH_ERR if any input parameter is not compatible with the expected type for that parameter.</p>
         *
         * @example
         * var msg = deviceapis.messaging.createMessage(deviceapis.messaging.TYPE_SMS);
         * msg.body = "WAC first SMS message.";
         * msg.to[0] = "+34666666666";
         */
        var UPDATE = 'UPDATE';
        function update(successCallback, errorCallback) {
            throw new DeviceAPIError(ax.NOT_SUPPORTED_ERR);
            /*
            function scb(message) {
                ax.util.invokeLater(null, successCallback, new Message(message));
            }

            return this.execAsyncWAC("update", scb, errorCallback, [peer.id]);
            */
        }

        ax.def(this)
            .property(ID, getId)
            .property(TYPE, getType, setType)
            .property(FOLDER, getFolder, setFolder)
            .property(TIMESTAMP, getTimestamp, setTimestamp)
            .property(FROM, getFrom, setFrom)
            .property(TO, getTo, setTo)
            .property(CC, getCc, setCc)
            .property(BCC, getBcc, setBcc)
            .property(BODY, getBody, setBody)
            .property(IS_READ, getIsRead, setIsRead)
            .property(PRIORITY, getPriority, setPriority)
            .property(SUBJECT, getSubject, setSubject)
            .property(ATTACHMENT, getAttachment, setAttachment)
            .method(UPDATE, update);
    }

    ////////////////////////////////////////////////////////////////////////////
    // typedef

    ax.def(g)
        .constant('MessageArray', Array)
        .constant('FileArray', Array);

    ///////////////////////////////////////////////////////////////////////
    // interface Messaging

    var MESSAGING = 'messaging';
    var CONSTRUCTOR_MESSAGING = 'Messaging';

    /**
     * <p>메시지를 생성하고 전송합니다.이 인터페이스는 createMessage 함수를 통해 Message 인터페이스의 객체를 생성하고 반환합니다. 반환된 메시지 객체는 Message 인터페이스에서 제공하는 sendMessage 함수로 전송할 수 있습니다.</p>
     *
     *  @class 메시지를 생성하고 전송합니다
     *  @name Messaging
     *
     *  @example
     * // Define the success callback
     * function messageSent() {
     *   alert("The SMS has been sent");
     * }
     *
     * // Define the error callback
     * function messageFailed(error) {
     *   alert("The SMS could not be sent " + error.message);
     * }
     *
     * // SMS sending example
     * var msg = deviceapis.messaging.createMessage(deviceapis.messaging.TYPE_SMS);
     * msg.body = "I will arrive in 10 minutes";
     * msg.to[0] = "+34666666666";
     *
     * // Send request
     * deviceapis.messaging.sendMessage(messageSent, messageFailed, msg);
     */
    function Messaging() {
    }

    /**
     * <p>SMS</p>
     * @constant
     * @memberOf Messaging#
     * @name TYPE_SMS
     */

    /**
     * <p>MMS</p>
     * @constant
     * @memberOf Messaging#
     * @name TYPE_MMS
     */

    /**
     * <p>E-Mail</p>
     * @constant
     * @memberOf Messaging#
     * @name TYPE_EMAIL
     */

    /**
     * <p>Inbox 폴더</p>
     * @constant
     * @memberOf Messaging#
     * @name FOLDER_INBOX
     */

    /**
     * <p>Outbox 폴더</p>
     * @constant
     * @memberOf Messaging#
     * @name FOLDER_OUTBOX
     */

    /**
     * <p>Draft 폴더</p>
     * @constant
     * @memberOf Messaging#
     * @name FOLDER_DRAFTS
     */

    /**
     * <p>Sentbox 폴더</p>
     * @constant
     * @memberOf Messaging#
     * @name FOLDER_SENTBO
     */


    // properties/methods of messaging

    var CREATE_MESSAGE = 'createMessage';
    var SEND_MESSAGE = 'sendMessage';
    var FIND_MESSAGES = 'findMessages';
    var ON_SMS = 'onSMS';
    var ON_MMS = 'onMMS';
    var ON_EMAIL = 'onEmail';
    var UNSUBSCRIBE = 'unsubscribe';


    /**
     * <p>특정 종류의 새 메시지를 생성합니다.</p>
     * @param {short} type <p>생성할 메시지의 종류. 지정 가능한 종류는 TYPE_SMS, TYPE_MMS, TYPE_EMAIL입니다.<br>
     * ※ Appspresso의 현재 버전 1.0 beta에서는 TYPE_SMS만 지원합니다.</p>
     *
     * @memberOf Messaging#
     * @type Message
     * @returns {Message} <p>지정한 종류의 메시지 객체. 메시지 생성에 실패한 경우엔 null을 반환합니다.</p>
     * @exception <p>INVALID_VALUES_ERR:입력된 매개변수가 유효한 값이 아닌 경우 <br>TYPE_MISMATCH_ERR:매개변수의 형식이 올바르지 않은 경우</p>
     *
     * @example
     * var msg = deviceapis.messaging.createMessage(deviceapis.messaging.TYPE_SMS);
     * msg.body = "WAC first SMS message.";
     */
    function createMessage(type) {
        type = validateType(type);
        return new Message({type: type});
    }

    /**
     * <p>메시지를 전송합니다.<br>메시지 종류가 전자메일이고 사용자가 여러 개의 계정을 설정해 놓은 경우 WAC 웹 런타임은 사용자에게 계정을 선택할 수 있는 창을 보여줄 수 있습니다. 만약 계정이 하나라면 WAC 웹 런타임은 이를 기본 계정으로 사용할 수 있습니다. 만약 계정이 없다면 WAC 웹 런타임은 계정을 생성할 수 있는 기능을 제공하던지 errorCallback을 호출할 수 있습니다.<br>
     * 메시지 전송에 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * •NOT_SUPPORTED_ERR:이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * 다음 표는 메시지 종류에 따라 Message 인터페이스에서 사용 가능한 속성들입니다.<br>
     * <table cellSpacing="1" cellPadding="1" width="100%" border="1">
     * <tbody>
     *     <tr align="center">
     *         <td style="background:#efefef">Attribute</td>
     *         <td style="background:#efefef">SMS</td>
     *         <td style="background:#efefef">MMS</td>
     *         <td style="background:#efefef">Email</td>
     *     </tr>
     *     <tr align="center">
     *         <td>to</td>
     *         <td>YES</td>
     *         <td>YES</td>
     *         <td>YES</td>
     *     </tr>
     *     <tr align="center">
     *         <td>body</td>
     *         <td>YES</td>
     *         <td>YES</td>
     *         <td>YES</td>
     *     </tr>
     *     <tr align="center">
     *         <td>subject</td>
     *         <td>NO</td>
     *         <td>YES</td>
     *         <td>YES</td>
     *     </tr>
     *     <tr align="center">
     *         <td>attachment</td>
     *         <td>NO</td>
     *         <td>YES</td>
     *         <td>YES</td>
     *     </tr>
     *     <tr align="center">
     *         <td>cc</td>
     *         <td>NO</td>
     *         <td>NO</td>
     *         <td>YES</td>
     *     </tr>
     *     <tr align="center">
     *         <td>bcc</td>
     *         <td>NO</td>
     *         <td>NO</td>
     *         <td>YES</td>
     *     </tr>
     *     <tr align="center">
     *         <td>priority</td>
     *         <td>NO</td>
     *         <td>NO</td>
     *         <td>YES</td>
     *     </tr>
     * </tbody>
     * </table>
     *
     * </p>
     *
     * @param {SuccessCallback} successCallback <p>메시지 전송에 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallback} errorCallback <p>메시지 전송에 실패한 경우 콜백으로 호출됩니다.</p>
     * @param {Message} message <p>전송할 메시지입니다.</p>
     * @function
     * @name sendMessage
     * @memberOf Messaging#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * var msg = deviceapis.messaging.createMessage(deviceapis.messaging.TYPE_SMS);
     * msg.body = "WAC first SMS message.";
     * msg.to[0] = "+34666666666";
     * deviceapis.messaging.sendMessage(
     *   function () {
     *     alert("brilliant - SMS sent");
     *   },
     *   function () {
     *     alert("epic failure");
     *   },
     *   msg
     * );
     */

    /**
     * 메시지를 전송합니다 (with per-recipient notification).
     *
     * @function
     * @name sendMessage_
     *
     * @param {MessageSendCallback} successCallback <p>메시지 전송에 성공한 경우 콜백으로 호출됩니다.</p>
     * <p>대상 목록에 있는 각각의 모든 수신자에 대해, 메세지가 성공적으로 전송되었을 때 successCallback의 onmessagesendsuccess가 호출 됩니다.</p>
     * <p>수신자에게 메시지를 전송할 수 없을 때 입력 매개변수의 에러코드, 수신자(번호)와 함께 successCallback의 onmessagesenderror가 호출 됩니다.</p>
     * <p>에러 조건에 따라 다음의 에러 코드가 전달 됩니다.</p>
     * • INVALID_VALUES_ERR: 입력된 매개변수가 유효한 값이 아닌 경우<br>
     * • NOT_SUPPORTED_ERR:이 피쳐가 지원되지 않는 경우<br>
     * • SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * • UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * <p>오퍼레이션이 완전히 완료되었을 때 메시지가 모든 수신자에게 성공적으로 전송되었다면 successCallback의 onsuccess가 호출 됩니다.</p>
     * @param {ErrorCallback} errorCallback <p>메시지 전송에 실패한 경우 콜백으로 호출됩니다.</p>
     * @param {Message} message <p>전송할 메시지입니다.</p>
     * @memberOf Messaging#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * // Define the send callback
     * var messageSendCallback = {
     *       onsuccess: function() {
     *         alert("The SMS has been sent to all the recipients");},
     *       onmessagesendsuccess: function(recipient) {
     *         alert("The SMS has been sent to " + recipient);},
     *       onmessagesenderror: function(error, recipient) {
     *         alert("The SMS has not been sent to " + recipient +
     *               " error " + error);}
     * };
     *
     * // Define the error callback
     * function messageFailed(error) {
     *     alert("The SMS could not be sent " + error.message);
     * }
     *
     * // SMS sending example
     * var msg = deviceapis.messaging.createMessage(deviceapis.messaging.TYPE_SMS);
     * msg.body = "I will arrive in 10 minutes";
     * msg.to = ["+34666666666", "+34888888888"];
     *
     * // Send request
     * deviceapis.messaging.sendMessage(messageSendCallback, messageFailed, msg);
     */
    function sendMessage(successCallbackObject, errorCallback, message) {

        ax.util.validateOptionalFunctionParam(errorCallback);
        validateMessage(message);

        if (!ax.isFunction(successCallbackObject) && !ax.isObject(successCallbackObject)){
            throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR);
        }

        var isIndividually = false;
        var handle = g_watchID++;//new Date().getTime();

        if ( ax.isObject(successCallbackObject) ) {
            ax.util.validateRequiredFunctionParam(successCallbackObject.onsuccess);
            ax.util.validateOptionalFunctionParam(successCallbackObject.onmessagesendsuccess);
            ax.util.validateOptionalFunctionParam(successCallbackObject.onmessagesenderror);
            isIndividually = true;
            callbackObjectList[handle] = successCallbackObject;
            callbackObjectList[handle].onerror = errorCallback;
        }else{
            callbackObjectList[handle] = {onsuccess: successCallbackObject, onerror: errorCallback};
        }

        callbackSuccessFlagList[handle] = true;

        //XXX: 예외... - althjs
        // successCallback: iOS/Android 모두 무시, 네이티브에서 callSuccessCallback을 호출함 (ax.nop를 인자로 넘김)
        // errorCallback: iOS: errorCallback 무시, 네이티브에서 callErrorCallback을 호출함
        // errorCallback: Android: errorCallback 사용 + 네이티브에서 callErrorCallback을 호출함
        return this.execAsyncWAC(SEND_MESSAGE, ax.nop, errorCallback, [message, handle, isIndividually]);
    }

    /**
     * <p>Gets an array of messages from the message folders matching the selected filter.<br>If any of the input parameters is not compatible with the expected type for that parameter a DeviceAPIError with code TYPE_MISMATCH_ERR MUST be synchronously thrown.<br>
     * If the this feature is not supported, a DeviceAPIError with code NOT_SUPPORTED_ERR MUST be returned in the errorCallback. If this functionality is not allowed the errorCallback MUST be invoked with a DeviceAPIError with code SECURITY_ERR.<br>
     * If the any of the input parameters contains an invalid value, a DeviceAPIError with code INVALID_VALUES_ERR MUST be returned.<br>
     * If the filter is passed and contains valid values, only those values in the message lists that matches the filter criteria as specified in the MessageFilter interface will be returned in the successCallback. If no filter is passed, the implementation MUST return the full list of messages in the successCallback. If no messages are available in the lists or no one matches the filter criteria, the successCallback will be invoked with an empty array.<br>
     * If an error occurs, while trying to retrieve the messages, the errorCallback function that was passed in the invocation MUST be called including a DeviceAPIError object with code UNKNOWN_ERR.<br>
     * In any of the cases in which the errorCallback should be invoked, if the developer has not passed an ErrorCallback or it is null, no action is required (i.e. the error is not notified to the developer).<br>
     * •    NOT_SUPPORTED_ERR: If this feature is not supported.<br>
     * •    SECURITY_ERR: If the operation is not allowed<br>
     * •    UNKNOWN_ERR: In any other error case.<br>
     * •    INVALID_VALUES_ERR: If the any of the input parameters contains an invalid value.<br></p>
     *
     * @param {FindMessagesSuccessCallback} successCallback <p>function called when the invocation ends successfully.</p>
     * @param {ErrorCallback} errorCallback <p>: function called when an error occurs.</p>
     * @param {MessageFilter} filter <p>message data to be used when filtering.</p>
     * @memberOf Messaging#
     * @type PendingOperation
     * @returns {PendingOperation} <p>PendingOperation in order to cancel the async call.</p>
     * @exception <p>with error code TYPE_MISMATCH_ERR if any input parameter is not compatible with the expected type for that parameter.</p>
     *
     */
    function findMessages(successCallback, errorCallback, filter) {
        // throw new DeviceAPIError(ax.NOT_SUPPORTED_ERR, 'not supported');
        var error = new DeviceAPIError(ax.NOT_SUPPORTED_ERR);
        if(ax.isFunction(errorCallback)) errorCallback(error);

        /*
        function scb(result) {
            var i, messages = [];

            for (i = 0; i < result.length; i++)
                messages[i] = new Message(result[i]);

            ax.util.invokeLater(null, successCallback, messages);
        }

        //ax.util.validateParamWAC(filter, true, false, 'object', true, 'filter');
        return this.execAsyncWAC(FIND_MESSAGES, scb, errorCallback, [convertFromDate(filter)]);
        */
    }

    /**
     * <p>Registers the function to be notified on incoming new SMSs<br>When this method is invoked, the implementation MUST register the function passed in the messageHandler argument as the handler for being notified whenever an incoming SMS arrives to the device. That function will be invoked every time an incoming SMS arrives, unless the unsubscribe method with the handler identifier is invoked in order to cancel the subscription.<br>
     * If the subscription is successfully created, an identifier for the handler is created and returned so that it is possible to cancel the subscription. If the subscription cannot be created, a DeviceAPIError is synchronously thrown with an error code that describes the reason for the error.<br>
     * </p>
     *
     * @param {OnIncomingMessage} messageHandler <p>The function to be invoked on incoming SMSs</p>
     * @memberOf Messaging#
     * @type long
     * @returns {long} <p>Subscription identifier</p>
     * @exception <p>with error code SECURITY_ERR if this operation is not allowed.<br>
     * with error code NOT_SUPPORTED_ERR if this feature is not supported.<br>
     * with error code TYPE_MISMATCH_ERR if any input parameter is not compatible with the expected type for that parameter.<br></p>
     *
     * @example
     * // function to receive new SMS notifications
     * function incomingSMS(message)
     * {
     *   alert("New incoming SMS from " + message.from);
     *
     *   // The subscription is cancelled to prevent further notifications
     *   if (mySMSListener != null)
     *     deviceapis.messaging.unsubscribe(mySMSListener);
     * }
     *
     * // Register listener for new SMS events
     * var mySMSListener = null;
     * mySMSListener = deviceapis.messaging.onSMS(incomingSMS);
     */
    function onSMS(messageHandler) {
        throw new DeviceAPIError(ax.NOT_SUPPORTED_ERR);
        /*
        var watchID;

        function scb(message) {
            g_watchIDs[watchID] = null;
            ax.util.invokeLater(null, messageHandler, new Message(message));
        }

        //ax.util.validateParamWAC(messageHandler, true, false, 'function', true, 'messageHandler');
        return watchID = this.watchWAC(ON_SMS, scb, null, []);
        */
    }

    /**
     * <p>Registers the function to be notified on incoming new MMSs<br>When this method is invoked, the implementation MUST register the function passed in the messageHandler argument as the handler for being notified whenever an incoming MMS arrives to the device. That function will be invoked every time an incoming MMS arrives, unless the unsubscribe method with the handler identifier is invoked in order to cancel the subscription.<br>
     * If the subscription is successfully created, an identifier for the handler is created and returned so that it is possible to cancel the subscription. If the subscription cannot be created, a DeviceAPIError is synchronously thrown with an error code that describes the reason for the error.<br>
     * </p>
     *
     * @param {OnIncomingMessage} messageHandler <p>The function to be invoked on incoming MMSs</p>
     * @memberOf Messaging#
     * @type long
     * @returns {long} <p>Subscription identifier</p>
     * @exception <p>with error code SECURITY_ERR if this operation is not allowed.<br>
     * with error code NOT_SUPPORTED_ERR if this feature is not supported.<br>
     * with error code TYPE_MISMATCH_ERR if any input parameter is not compatible with the expected type for that parameter.<br></p>
     *
     * @example
     * // function to receive new MMS notifications
     * function incomingMMS(message)
     * {
     *   alert("New incoming MMS from " + message.from);
     *
     *   // The subscription is cancelled to prevent further notifications
     *   if (myMMSListener != null)
     *     deviceapis.messaging.unsubscribe(myMMSListener);
     * }
     *
     * // Register listener for new MMS events
     * var myMMSListener = null;
     * myMMSListener = deviceapis.messaging.onMMS(incomingMMS);
     */
    function onMMS(messageHandler) {
        throw new DeviceAPIError(ax.NOT_SUPPORTED_ERR);
        /*
        var watchID;

        function scb(message) {
            g_watchIDs[watchID] = null;
            ax.util.invokeLater(null, messageHandler, new Message(message));
        }

        //ax.util.validateParamWAC(messageHandler, true, false, 'function', true, 'messageHandler');
        return watchID = this.watchWAC(ON_MMS, scb, null, []);
        */
    }

    /**
     * <p>Registers the function to be notified on incoming new Email<br>When this method is invoked, the implementation MUST register the function passed in the messageHandler argument as the handler for being notified whenever an incoming Email arrives to the device. That function will be invoked every time an incoming Email arrives, unless the unsubscribe method with the handler identifier is invoked in order to cancel the subscription.<br>
     * If the subscription is successfully created, an identifier for the handler is created and returned so that it is possible to cancel the subscription. If the subscription cannot be created, a DeviceAPIError is synchronously thrown with an error code that describes the reason for the error.<br>
     * </p>
     *
     * @param {OnIncomingMessage} messageHandler <p>The function to be invoked on incoming emails</p>
     * @memberOf Messaging#
     * @type long
     * @returns {long} <p>Subscription identifier</p>
     * @exception <p>with error code SECURITY_ERR if this operation is not allowed.<br>
     * with error code NOT_SUPPORTED_ERR if this feature is not supported.<br>
     * with error code TYPE_MISMATCH_ERR if any input parameter is not compatible with the expected type for that parameter.</p>
     *
     * @example
     * // function to receive new Email notifications
     * function incomingEmail(message)
     * {
     *   alert("New incoming Email from " + message.from);
     *
     *   // The subscription is cancelled to prevent further notifications
     *   if (myEmailListener != null)
     *     deviceapis.messaging.unsubscribe(myEmailListener);
     * }
     *
     * // Register listener for new Email events
     * var myEmailListener = null;
     * myEmailListener = deviceapis.messaging.onEmail(incomingEmail);
     */
    function onEmail(messageHandler) {
        throw new DeviceAPIError(ax.NOT_SUPPORTED_ERR);
        /*
        var watchID;

        function scb(message) {
            g_watchIDs[watchID] = null;
            ax.util.invokeLater(null, messageHandler, new Message(message));
        }

        //ax.util.validateParamWAC(messageHandler, true, false, 'function', true, 'messageHandler');
        return watchID = this.watchWAC(ON_EMAIL, scb, null, []);
        */
    }

    /**
      * <p>Cancels a messaging subscription<br>If the subscriptionHandler argument is valid and corresponds to a subscription already in place the subscription process MUST immediately stop and no further message notifications MUST be invoked. If the subscriptionHandler argument does not correspond to a valid subscription, the method should return without any further action.</p>
      * @param {long} subscriptionHandler <p>identifier of the subscription returned by the onSMS(), onMMS() or onEmail() methods</p>
      * @memberOf Messaging#
      * @type void
      * @returns {void}
      * @exception <p>with error code TYPE_MISMATCH_ERR if the input parameter is not compatible with the expected type for that parameter.</p>
      */
    function unsubscribe(subscriptionHandler) {
        throw new DeviceAPIError(ax.NOT_SUPPORTED_ERR);
        /*
        //ax.util.validateParamWAC(subscriptionHandler, true, false, 'number', true, 'subscriptionHandler');

        if (!(subscriptionHandler in g_watchIDs))
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR, 'invalid subscriptionHandler');

        delete g_watchIDs[subscriptionHandler];
        this.stopWatchWAC(UNSUBSCRIBE, subscriptionHandler - 0);
        */
    }

    var callbackObjectList = {};
    var callbackSuccessFlagList = {};

    // 네이티브에서 직접호출
    function callSuccessCallback(handle, completed, recipient) {
        var callback = callbackObjectList[handle];

        if (!!callback.onmessagesendsuccess && !!recipient) {
            if(ax.isArray(recipient)){
                for (var i in recipient) {
                    callback.onmessagesendsuccess(recipient[i]);
                }
            }else{
                callback.onmessagesendsuccess(recipient);
            }
        }

        if(completed) {
            if (callbackSuccessFlagList[handle]){
                callback.onsuccess();    //모두 성공한 경우
            }else{
                callErrorCallback(handle, true);
                return;
            }
            callbackObjectList[handle] = null;
            callbackSuccessFlagList[handle] = null;
        }
    }

    // 네이티브에서 직접호출
    function callErrorCallback(handle, completed, recipient) {
        var callback = callbackObjectList[handle],
            error = new DeviceAPIError(ax.UNKNOWN_ERR);

        if (!!callback.onmessagesenderror && !!recipient) {
            if(ax.isArray(recipient)){
                for (var i in recipient) {
                    callback.onmessagesenderror(error, recipient[i]);
                }
            }else{
                callback.onmessagesenderror(error, recipient);
            }
            callbackSuccessFlagList[handle] = false;
        }

        if(completed) {
            deviceapis.errorAsyncWAC(error, callback.onerror);
            callbackObjectList[handle] = null;
            callbackSuccessFlagList[handle] = null;
        }
    }


// ====================================================
Messaging.prototype = ax.plugin('deviceapis.messaging', {
    'createMessage': createMessage,
    'sendMessage': sendMessage,
    'findMessages': findMessages,
    'onSMS': onSMS,
    'onMMS': onMMS,
    'onEmail': onEmail,
    'unsubscribe': unsubscribe,
    'callSuccessCallback': callSuccessCallback,
    'callErrorCallback': callErrorCallback
});
ax.def(Messaging)
    .constant('TYPE_SMS', TYPE_SMS)
    .constant('TYPE_MMS', TYPE_MMS)
    .constant('TYPE_EMAIL', TYPE_EMAIL)
    .constant('FOLDER_INBOX', FOLDER_INBOX)
    .constant('FOLDER_OUTBOX', FOLDER_OUTBOX)
    .constant('FOLDER_DRAFTS', FOLDER_DRAFTS)
    .constant('FOLDER_SENTBOX', FOLDER_SENTBOX);
ax.def(Messaging.prototype)
    .constant('TYPE_SMS', TYPE_SMS)
    .constant('TYPE_MMS', TYPE_MMS)
    .constant('TYPE_EMAIL', TYPE_EMAIL)
    .constant('FOLDER_INBOX', FOLDER_INBOX)
    .constant('FOLDER_OUTBOX', FOLDER_OUTBOX)
    .constant('FOLDER_DRAFTS', FOLDER_DRAFTS)
    .constant('FOLDER_SENTBOX', FOLDER_SENTBOX);
ax.def(g)
    .constant('Message', Message)
    //TODO: .constant('Message', MessageFilter)
    //TODO: .constant('Message', MessageAttachment)
    .constant('Messaging', Messaging);
ax.def(deviceapis).constant('messaging', new Messaging());
}(window));



/**
 * <p>Filter to restrict the items returned by the findMessages method.<br>When used this filter in the findMessages operation, the result-set of the search MUST only contain the Message entries that match the filter values.<br>
 * An entry matches the filter, if the attributes of the entry matches all the attributes of the filter with values different to undefined or null. I.e. the search is performed in a similar manner to a SQL "AND" operation.<br>
 * An attribute of the Message entry matches the filter value according to the following rules:<br>
 * •    For filter attributes of type DOMString an entry matches this value if its corresponding attribute is exactly the same than the filter one unless the filter contains U+0025 'PERCENT SIGN' wildcard character(s). If wildcards are used, the behavior is similar to the LIKE condition in SQL ('%' matches any string of any length - including zero length).<br>
 * •    For filter attributes of type StringArray the same rules as for filter attributes of type DOMString apply for each of the fields within the given Array separately. The search for all included fields is performed similar to a SQL "AND" operation in the end without taking into account the (possible) difference in ordering between Message fields as well as MessageFilter fields.<br>
 * •    For filter attributes of an array of WebIDL numeric type (type), an entry matches it only if the corresponding entry attribute has exactly the same value as any of the array elements.<br>
 * •    For filter attributes of any WebIDL boolean type (isRead, messagePriority) an entry matches it only if the corresponding entry attribute has exactly the same state (i.e. true or false).<br>
 * •    For message attributes of type Date (i.e. date), a couple of filter attributes are included (initialDate, endDate), in order to allow looking for messages between two dates. If both initialDate and endDate are different to null, a message matches the filter if its date attribute is between initialDate and endDate. If only initialDate contains a value different to null, a message matches the filter if its date is later than initialDate. If only endDate contains a value different to null, a message matches the filter if its date is earlier than endDate.<br>
 * </p>
 * @class
 * @name MessageFilter
 */

/**
 * <p>Used for filtering the Message id attribute.<br>Messages which id corresponds with this attribute (either exactly or with the specified wildcards) match this filtering criteria.</p>
 * @filed
 * @type DOMString
 * @memberOf MessageFilter#
 * @name id
 */

/**
 * <p>Used for filtering the Message type attribute.<br>Messages with type equals to one of the values in this array match the filtering criteria. </p>
 * @filed
 * @type ShortArray
 * @memberOf MessageFilter#
 * @name type
 */

/**
 * <p>Used for filtering the Message folder attribute.<br>Messages with folder equals to one of the values in this array match the filtering criteria. </p>
 * @filed
 * @type ShortArray
 * @memberOf MessageFilter#
 * @name folder
 */

/**
 * <p>Used for filtering the Message timestamp attribute.Messages with date later than this attribute match the filtering criteria.</p>
 * @filed
 * @type Date
 * @memberOf MessageFilter#
 * @name startTimestamp
 */

/**
 * <p>Used for filtering the Message timestamp attribute.<br>Messages with date earlier than this attribute match the filtering criteria. </p>
 * @filed
 * @type Date
 * @memberOf MessageFilter#
 * @name endTimestamp
 */

/**
 * <p>Used for filtering the Message from attribute.<br>Messages which from corresponds with this attribute (either exactly or with the specified wildcards) match this filtering criteria. </p>
 * @filed
 * @type DOMString
 * @memberOf MessageFilter#
 * @name from
 */

/**
 * <p>Used for filtering the Message to attribute.<br>Messages which elements in the to array that correspond to all the elements of this attribute (either exactly or with the specified wildcards) match this filtering criteria.</p>
 * @filed
 * @type StringArray
 * @memberOf MessageFilter#
 * @name to
 */

/**
 * <p>Used for filtering the Message cc attribute.<br>Messages which elements in the cc array that correspond to all the elements of this attribute (either exactly or with the specified wildcards) match this filtering criteria.</p>
 * @filed
 * @type StringArray
 * @memberOf MessageFilter#
 * @name cc
 */

/**
 * <p>Used for filtering the Message bcc attribute.<br>Messages which elements in the bcc array that correspond to all the elements of this attribute (either exactly or with the specified wildcards) match this filtering criteria</p>
 * @filed
 * @type StringArray
 * @memberOf MessageFilter#
 * @name bcc
 */

/**
 * <p>Used for filtering the Message body attribute.<br>Messages which body corresponds with this attribute (either exactly or with the specified wildcards) match this filtering criteria. </p>
 * @filed
 * @type DOMString
 * @memberOf MessageFilter#
 * @name body
 */

/**
 * <p>Used for filtering the Message isRead attribute.<br>Messages which isRead corresponds exactly with this attribute match this filtering criteria. </p>
 * @filed
 * @type boolean
 * @memberOf MessageFilter#
 * @name isRead
 */

/**
 * <p>Used for filtering the Message messagePriority attribute.<br>Messages which messagePriority corresponds exactly with this attribute match this filtering criteria. </p>
 * @filed
 * @type boolean
 * @memberOf MessageFilter#
 * @name messagePriority
 */

/**
 * <p>Used for filtering the Message subject attribute.<br>Messages which subject corresponds with this attribute (either exactly or with the specified wildcards) match this filtering criteria.</p>
 * @filed
 * @type DOMString
 * @memberOf MessageFilter#
 * @name subject
 */



/**
 * <p>Interface for specifying the method called on new incoming message events.<br>This interface specifies a function that will provide a message object that represents the received message. It is used in the onSMS(), onMMS(), onEmail() method invocation.</p>
 * @class
 * @name OnIncomingMessage
 */

/**
 * <p>Method invoked when an incoming message is received.</p>
 * @param {Message} message <p>The message received</p>
 * @function
 * @type void
 * @return void
 * @memberOf OnIncomingMessage#
 * @name onevent
 */

/**
 * <p>findMessages specific success callback.</p>
 * @namespace
 * @name FindMessagesSuccessCallback
 */

/**
 * <p>Method invoked when the asynchronous call completes successfully</p>
 * @function
 * @param {MessageArray} messages <p>The list of messages that correspond to the find criteria</p>
 * @return {void}
 * @memberOf FindMessagesSuccessCallback
 * @name onsuccess
 */

/**
 * <p>update specific success callback.</p>
 * @namespace
 * @name UpdateMessageSuccessCallback
 */

/**
 * <p>Method invoked when the asynchronous call completes successfully.</p>
 * @function
 * @param {Message} message <p>The new message representing the actual updated status</p>
 * @return {void}
 * @memberOf UpdateMessageSuccessCallback
 * @name onsuccess
 */

/**
 * 각각의 개별적인 수신자의 메시지 전송 결과를 위해 호출되는 메소드를 정의하는 인터페이스
 * @namespace
 * @name MessageSendCallback
 */

/**
 * 모든 수신자에게 메시지가 성공적으로 전송되었을 때 호출되는 메소드
 * @function
 * @name onsuccess
 * @memberOf MessageSendCallback
 * @return {void}
 */

/**
 * 개별적인 수신자를 포함하는 메시지 전송 결과 때마다. 또는 모든 수신자에게 성공적으로 전송되었을 때 호출되는 메소드의 집합을 정의하는 인터페이스.
 * @function
 * @name onmessagesendsuccess
 * @param {DOMString} recipient 수신자
 * @memberOf MessageSendCallback
 * @return {void}
 */

/**
 * 메시지가 개별 수신자에게 전송되는데 실패했을 때 호출되는 메소드
 * @function
 * @name onmessagesenderror
 * @param {DeviceAPIError} error <p>오류객체</p>
 * @param {DOMString} recipient 수신자
 * @memberOf MessageSendCallback
 * @return {void}
 */

