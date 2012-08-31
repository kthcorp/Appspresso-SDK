////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview <h2 class="filename">Camera Module</h2><p>이 API는 단말의 카메라를 제어하여 사진 촬영과 동영상 녹화를 가능케 하며 카메라의 뷰파인더를 보여줄 수 있게 합니다. 사용 가능한 카메라의 형태나 개수는 단말에 따라 다를 수 있으며 모든 카메라가 지원되지 않을 수 있습니다. 이 API는 최소한 주 카메라에 대한 접근을 제공합니다.
 * getCameras 함수를 통해 이 API를 통해 제어할 수 있는 모든 카메라의 배열을 얻을 수 있습니다. 이 배열의 첫 번째 원소는 (대개의 경우 후면에 위치하는) 주 카메라입니다.
 * 사진 촬영 혹은 동영상을 녹화할 때 파일이 저장될 위치를 지정할 수 있습니다. 촬영과 녹화는 비동기적으로 수행됩니다.</p>
 * <p>
 * http://wacapps.net/api/camera 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 CameraManager 인터페이스의 인스턴스가 deviceapis.camera로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/camera</strong><br>
 * 모든 기능을 사용할 수 있음<br>
 * <strong>http://wacapps.net/api/camera.show</strong><br>
 * captureImage()와 startVideoCapture()을 제외한 모든 기능을 이용할 수 있음<br>
 * <strong>http://wacapps.net/api/camera.capture</strong><br>
 * createPreviewNode()를 제외한 모든 기능을 사용할 수 있음<br>
 * ※ setWindow 함수에 대한 설명은 Waikiki API 2.0 beta 명세의 오류입니다. setWindow 함수는 Waikiki API 2.0 beta에서 사라진 함수입니다. Waikiki API 2.0 정식 명세에서는 이 오류가 정정되었습니다.<br>
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
    // typedef

    ax.def(g).constant('CameraArray', Array);

    ////////////////////////////////////////////////////////////////////////////
    // convenient functions, variables

    var monitorInfo;    // {cameraID: {camera, timerID_start, timerID_stop, previewNode, previewNodeID, previewing, capturing}}
    var NODE_ID_PREFIX = '_AppspressoCameraPreviewNode';

    function createNode(nodeID) {
        var node = document.getElementById(nodeID);

        if (!node) {
            node = document.createElement('div');
            node.id = nodeID;
            // node.style.zIndex = -999;    // preview node를 가장 아래쪽으로 배치
            return node;
        }

        throw new DeviceAPIError(ax.INVALID_STATE_ERR, 'preview node already exists');
    }

    function getNode(nodeID) {
        return document.getElementById(nodeID);
    }

    function checkPreview(camera) {
        //TODO: jstestdriver에서 아래 한줄이면 테스트 돌릴 수 있음... 검토 필요 - althjs
        //if(!!_APPSPRESSO_TEST) return;
        if (!monitorInfo[camera.id].previewing) {
            var msg = 'preview node is not found or not visible';
            if(_DEBUG) { ax.debug(msg); }
            throw new DeviceAPIError(ax.INVALID_STATE_ERR, msg);
        }
    }

    function checkPreviousImgCapture(camera) {
        if (monitorInfo[camera.id].imgCapturing) {
            throw new DeviceAPIError(ax.INVALID_STATE_ERR, 'previous capturing image being processed');
        }
    }

    function checkPreviousVdoCapture(camera) {
        if (monitorInfo[camera.id].vdoCapturing) {
            throw new DeviceAPIError(ax.INVALID_STATE_ERR, 'previous capturing video being processed');
        }
    }

    function getAbsolutePosition(element){
        var top = 0, left = 0;
        do {
            top += element.offsetTop  || 0;
            left += element.offsetLeft || 0;
            element = element.offsetParent;
        } while(element);

        return {
            "top":top,
            "left":left
        };
    }

    function checkOptions(options) {
        ax.util.validateOptionalObjectParam(options);

        if (options) {
            ax.util.validateOptionalStringParam(options.destinationFilename);
            ax.util.validateOptionalBooleanParam(options.highRes);

            if( options.destinationFilename && !ax.util.isValidPath(options.destinationFilename) ){
                throw new DeviceAPIError(ax.INVALID_VALUES_ERR);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // interface CameraManager
    /**
     * <p>카메라를 관리하기 위한 마스터 인터페이스입니다. 이 인터페이스는 단말의 카메라들에 접근할 수 있는 함수를 제공합니다. getCameras 함수는 사용 가능한 카메라들에 대한 참조를 제공합니다. 카메라에 대한 참조를 통해 사진 촬영과 동영상 녹화를 할 수 있습니다. 이 인터페이스는 CameraManagerObject 인터페이스에 의해 deviceapis 객체의 속성으로 제공됩니다.</p>
     * @class 카메라를 관리하기 위한 마스터 인터페이스입니다.
     * @name CameraManager
     */
    function CameraManager() {
    }

    /**
     * <p>지원 가능한 카메라들을 제공합니다. 이 함수에 대한 호출이 성공하면 지원 가능한 모든 카메라들을 포함하는 배열을 매개변수로 successCallback 함수를 호출합니다. 주 카메라(대개의 경우 후면 카메라)는 항상 배열의 첫 번째 원소가 됩니다. 만약 사용 가능한 카메라가 없다면 비어있는 배열을 매개변수로 successCallback을 호출합니다.
     * 만약 오류가 발생했다면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * </p>
     *
     * @param {CameraArraySuccessCallback} successCallback <p>카메라 배열을 얻는데 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallback} errorCallback <p>카메라 배열을 얻는데 실패한 경우 콜백으로 호출됩니다.</p>
     * @memberOf CameraManager#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     * @memberOf CameraManager.prototype
     *
     * @example
     * //variable to hold main camera
     * var mainCamera;
     *
     * //success callback
     * function onCamerasObtained(cams) {
     *   if(cams.length > 0) {
     *     alert("found " + cams.length + " cameras");
     *     //store main camera
     *     mainCamera = cams[0];
     *   } else {
     *     alert("no cameras found");
     *   }
     * }
     *
     * //error callback
     * function onGetCamerasError(error) {
     *   alert(error.message);
     * }
     *
     * //call async function to retrieve all cameras on device
     * try {
     *   var pendop = deviceapis.camera.getCameras(
     *       onCamerasObtained, onGetCamerasError);
     * } catch (exp) {
     *   alert("getCameras Exception :[" + exp.code + "] " + exp.message);
     * }
     */
    function getCameras(successCallback, errorCallback) {
        ax.util.validateRequiredFunctionParam(successCallback);

        function scb(result) {
            try {
                var cameras = [];
                monitorInfo = {};

                var i;
                for (i = 0; i < result.length; i++) {
                    cameras[i] = new Camera(result[i]);
                    monitorInfo[cameras[i].id] = {camera: cameras[i]};
                }

                ax.util.invokeLater(null, successCallback, cameras);
            }
            catch (e) {
                var err = new DeviceAPIError(ax.UNKNOWN_ERR, e.message);
                ax.util.invokeLater(null, errorCallback, err);
            }
        }

        return this.execAsyncWAC('getCameras', scb, errorCallback);
    }

    function stopVideoCaptureOf(id) {
        if(monitorInfo[id].vdoCapturing) {
            monitorInfo[id].vdoCapturing = false;
            monitorInfo[id].vdoCapturingSCB(monitorInfo[id].vdoFileName);
            monitorInfo[id].vdoFileName = null;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // interface Camera

    /**
     * <p>단말의 카메라에 대한 접근을 제공합니다. 이 인터페이스는 위젯에서 단말의 카메라를 이용하여 사진과 동영상을 촬영하거나 뷰파인더를 통한 미리보기를 가능하게 합니다.</p>
     * @class 단말의 카메라에 대한 접근을 제공합니다.
     * @name Camera
     */
    function Camera(camera) {
        var cameraID = camera.id;

        this.__defineSetter__('id', function(v) {});
        this.__defineGetter__('id', function() { return cameraID; });
    }

    /**
     * <p>카메라 식별자. 카메라에 대한 식별자로서 사람이 읽고 이해할 수 있는 문자열입니다. e.g. “front”</p>
     * @type DOMString
     * @memberOf Camera#
     * @field
     * @name id
     */

    // properties/methods of Camera

    var CAPTURE_IMAGE = 'captureImage';
    var START_VIDEO_CAPTURE = 'startVideoCapture';
    var STOP_VIDEO_CAPTURE = 'stopVideoCapture';
    var CREATE_PREVIEW_NODE = 'createPreviewNode';

    /**
     * <p>비동기적으로 사진을 촬영하고 파일로 저장합니다. 사진 촬영에 성공하면 저장된 파일의 경로를 매개변수로 하여 successCallback 함수가 호출됩니다.
     * 촬영에 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수가 호출됩니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * </p>
     *
     * @param {CameraCaptureSuccessCallback} successCallback <p>촬영에 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallback} errorCallback <p>촬영에 실패한 경우 콜백으로 호출됩니다.</p>
     * @param {CameraOptions} options <p>사진 촬영에 적용되는 선택사항입니다.</p>
     * @memberOf Camera#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * //variable to hold pending operation for captureImage
     * var op = null;
     *
     * //success callback
     * function onCaptureImageSuccess(filename) {
     *   alert("Captured image path:" + filename);
     *   op = null;
     * }
     *
     * //error callback
     * function onCaptureImageError(e) {
     *   alert("Capture image failed with error:" + e.message);
     *   op = null;
     * }
     *
     * //call async function to capture an image
     * // it is assumed that mainCamera is obtained previously
     * // using deviceapis.camera.getCameras
     * try {
     *   op = mainCamera.captureImage(
     *       onCaptureImageSuccess,
     *       onCaptureImageError,
     *       {destinationFilename:"images/a.jpg", highRes:true}
     *   );
     * } catch(exp) {
     *   alert("captureImage Exception :[" + exp.code + "] " + exp.message);
     * }
     */
    function captureImage(successCallback, errorCallback, options) {
        ax.util.validateRequiredFunctionParam(successCallback);
        ax.util.validateOptionalFunctionParam(errorCallback);

        var self = this;

        try{
            checkPreview(this);
            checkPreviousImgCapture(this);
            checkPreviousVdoCapture(this);
            checkOptions(options);
        } catch(e) {
            this.errorAsyncWAC(e, errorCallback);
            return;
        }

        function scb(fileName) {
            monitorInfo[self.id].imgCapturing = false;
            ax.util.invokeLater(null, successCallback, fileName);
//            (function() {
//                var _self = self, _cb = successCallback, _fileName = fileName;
//
//                // 사진 찍자 마자 또 다시 사진 찍으려고 하면 죽는 경우가 빈번해서 약간의 지연 시간 100ms를 준다
//                window.setTimeout(function() {
//                    try {
//                        monitorInfo[_self.id].imgCapturing = false;
//                        _cb.call(null, _fileName);
//                    }
//                    catch (e) {
//                        if(_DEBUG) { ax.debug('uncaught exception from function {1}: {0} ', e, ax.util.getFunctionName(_cb)); }
//                    }
//                }, 100);
//            })();
        }

        function ecb(e) {
            monitorInfo[self.id].imgCapturing = false;
            self.errorAsyncWAC(e, errorCallback);
        }

        try {
            var po = this.execAsyncWAC(CAPTURE_IMAGE, scb, ecb, [this.id, options || {}]);
            monitorInfo[this.id].imgCapturing = true;
            return po;
        }
        catch (e) {
            monitorInfo[this.id].imgCapturing = false;
            throw e;
        }
    }

    /**
     * <p>동영상 녹화를 시작합니다. 동영상 녹화를 시작합니다. 녹화가 시작되면 stopVideo 함수를 호출하거나 플랫폼의 제한(파일 크기나 녹화 지속 시간에 대한 제한)으로 인해 녹화가 중단될 때까지 녹화를 계속합니다. 녹화가 중지되고 파일을 저장하는 데 성공하면 저장된 파일의 경로를 매개변수로 하여 successCallback 함수가 호출됩니다.
     * 만약 녹화를 시작할 수 없거나 녹화 도중 오류가 발생하여 파일을 저장할 수 없는 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수가 호출됩니다. 발생 가능한 에러 코드는 다음과 같습니다. <br>
     * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * 비디오 녹화는 반환 받은 PendingOperation 객체를 통해 언제든지 취소될 수 있습니다. PendingOperaton 객체의 cancel 함수는 녹화가 중지된 후에 제어권을 반환합니다.<br>
     * ※ Appspresso 현재 버전 1.0 beta에서는 PendingOperation을 통해 녹화를 취소해도 녹화가 계속되는 버그가 있습니다. 반드시 stopVideoCapture 함수 호출로 녹화를 중단해야 합니다.
     * </p>
     *
     * @param {CameraCaptureSuccessCallback} successCallback <p>녹화 시작에 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallback} errorCallback <p>녹화 시작에 실패한 경우 콜백으로 호출됩니다.</p>
     * @param {CameraOptions} options <p>녹화와 관련된 선택 사항을 지정합니다.</p>
     * @memberOf Camera#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * //variable to hold pending operation for startVideoCapture
     * var op = null;
     *
     * //success callback
     * function onCaptureVideoSuccess(filename) {
     *   alert("Captured video path:" + filename);
     *   op = null;
     * }
     *
     * //error callback
     * function onCaptureVideoError(e) {
     *   alert("Capture video failed with error:" + e.message);
     *   op = null;
     * }
     *
     * //call async function to start a video capture
     * //it is assumed that mainCamera is obtained previous using deviceapis.camera.getCameras
     * try {
     *   op = mainCamera.startVideoCapture(
     *       onCaptureVideoSuccess,
     *       onCaptureVideoError,
     *       {destinationFilename:"videos/a.3gp", highRes:true}
     *   );
     * } catch(exp) {
     *   alert("captureImage Exception :[" + exp.code + "] " + exp.message);
     * }
     */
    function startVideoCapture(successCallback, errorCallback, options) {
        ax.util.validateRequiredFunctionParam(successCallback);

        var self = this;

        try{
            checkPreview(this);
            checkPreviousImgCapture(this);
            checkPreviousVdoCapture(this);
            checkOptions(options);
        } catch(e) {
            this.errorAsyncWAC(e, errorCallback);
            return;
        }

        function scb(fileName) {
            monitorInfo[self.id].vdoCapturing = true;
            monitorInfo[self.id].vdoFileName = fileName;
            monitorInfo[self.id].vdoCapturingSCB = successCallback;
        }

        return this.execAsyncWAC(START_VIDEO_CAPTURE, scb, errorCallback, [this.id, options || {}]);
    }

    /**
     * <p>비디오 녹화를 중지합니다.이 함수는 startVideoCapture 함수가 호출되어 동영상 녹화가 진행 중일 때만 호출 가능합니다. 만약 동영상 녹화 중이 아닐 때 이 함수를 호출하면 무시됩니다. 이 함수를 호출하면 녹화는 중지되고 동영상 파일이 생성되어 저장됩니다. 그리고 저장된 파일의 경로를 매개변수로 startVideoCapture 함수에서 전달한 successCallback 함수가 호출됩니다. 만약 이 과정 중 에러가 발생하면 마찬가지로 startVideoCapture 함수에서 전달한 errorCallback 함수가 호출됩니다.<br>
     * ※ 현재 버전의 Appspresso 1.0 beta에서는 동영상 녹화 중이 아닐 때 이 함수를 호출하면 예외를 던집니다(throw). 또한 stopVideoCapture 함수를 호출할 때가 아닌 startVideoCapture 함수를 호출하면 즉시 successCallback 함수가 호출됩니다. 이 처리 방식은 이후 버전에서 변경될 예정입니다.
     * </p>
     *
     * @memberOf Camera#
     * @type void
     * @returns {void}
     *
     * @example
     * //variable to hold pending operation for startVideoCapture
     * var op = null;
     *
     * //success callback
     * function onCaptureVideoSuccess(filename) {
     *   alert("Captured video path:" + filename);
     *   op = null;
     * }
     *
     * //error callback
     * function onCaptureVideoError(e) {
     *   alert("Capture video failed with error:" + e.message);
     *   op = null;
     * }
     *
     * //call async function to start a video capture
     * //it is assumed that mainCamera is obtained previous using deviceapis.camera.getCameras
     * try {
     *   op = mainCamera.startVideoCapture(
     *       onCaptureVideoSuccess,
     *       onCaptureVideoError,
     *       {destinationFilename:"videos/a.3gp", highRes:true}
     *   );
     * } catch(exp) {
     *   alert("captureImage Exception :[" + exp.code + "] " + exp.message);
     * }
     *
     * //to be invoked when the user decides to stop the video recording
     * function stopVideo() {
     *   // if the op is different to null we request to stop the video recording
     *   if (op != null)
     *     mainCamera.stopVideoCapture();
     *   else
     *     alert("Video recording cannot be stopped as it already finished");
     * }
     *
     * //to be invoked when the user decides to cancel the video recording
     * //no video will be captured
     * function cancelVideo() {
     *   //if the op is null, the operation is already over
     *   if (op != null) {
     *     op.cancel();
     *     op = null;
     *   } else
     *     alert("Video recording cannot be cancelled as it already finished");
     * }
     */
    function stopVideoCapture() {
        if (!monitorInfo[this.id].vdoCapturing || !monitorInfo[this.id].vdoCapturingSCB) {
            if(_DEBUG) { ax.debug('no capturing being processed'); }
            return;
        }

        monitorInfo[this.id].vdoCapturing = false;
        var scb = monitorInfo[this.id].vdoCapturingSCB;
        monitorInfo[this.id].vdoCapturingSCB = null;

         try {
                this.execSync(STOP_VIDEO_CAPTURE, [this.id]);
                ax.util.invokeLater(null, scb, monitorInfo[this.id].vdoFileName);
         } catch(e) {
            ax.uti.invokeLater(null, errorCallback, e);
            return;
         }
    }

    /**
     * <p>카메라의 미리보기를 보여주기 위한 DOM 객체를 생성하여 반환합니다. 이 함수는 카메라의 미리보기를 보여줄 DOM 객체를 생성하여 반환합니다. 반환된 DOM 객체를 document의 DOM 계층구조 상에 있는 적당한 부모 요소에 자식 요소로 추가하고 반환된 DOM 객체의 위치와 크기를 조정하는 것으로 카메라 미리보기의 위치와 크기를 지정할 수 있습니다. 숨겨진 상태인 이 DOM 객체를 보이도록 변경하면 카메라 미리보기가 시작됩니다. 미리보기가 시작된 후 이 DOM 객체가 보이지 않는 상태가 되거나 DOM 계층구조에서 제거되거나 스크롤되어 뷰포트 영역 밖으로 나가면 미리보기가 중지됩니다. 따라서 미리보기를 중지하려면 이 DOM 객체를 보이지 않는 상태로 변경하고 DOM 계층구조에서 제거하면 됩니다.
     * 만약 오류가 발생하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수가 호출됩니다. 발생 가능한 에러 코드는 다음과 같습니다.
     * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우.<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * ※ 현재 버전의 Appspresso 1.0 beta에서는 DOM 객체를 다루는 데 몇 가지 제약이 있습니다. Android 런타임의 경우 카메라 미리보기가 HTML 문서 상의 다른 요소를 가릴 수 있습니다. iOS 런타임의 경우에는 반환된 DOM 객체의 일부 영역이 다른 요소에 의해 가려져도 미리보기는 전체 크기로 보여집니다. 따라서 반환된 DOM 객체를 HTML 문서 상의 다른 요소와 겹치지 않게 배치할 것을 권고합니다.
     * </p>
     *
     * @param {CameraPreviewSuccessCallback} successCallback <p>미리보기를 위한 DOM객체의 생성에 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {ErrorCallBack} errorCallback <p>미리보기를 위한 DOM 객체의 생성에 실패한 경우 콜백으로 호출됩니다.</p>
     * @memberOf Camera#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * //variable that holds pending operation
     * var op = null;
     *
     * //variable that holds id of preview element
     * var myPreviewId = null;
     *
     * //retrieve a 'container' node which can house the preview window object
     * var myParent = document.getElementById("myDiv");
     *
     * //success callback
     * function onCreatePreviewNodeSuccess(previewObject) {
     *   myPreviewId = previewObject.id;
     *   myParent.appendChild(previewObject);
     *   previewObject.style.visibility = "visible"; //start preview
     *   op = null;
     * }
     *
     * //error callback
     * function onCreatePreviewNodeError(e) {
     *   alert("Create preview node failed with error:" + e.message);
     *   op = null;
     * }
     *
     * //call async function to create camera preview DOM object
     * //it is assumed that mainCamera is obtained previous using deviceapis.camera.getCameras
     * try {
     *   op = mainCamera.createPreviewNode(
     *       onCreatePreviewNodeSuccess, onCreatePreviewNodeError);
     * } catch(exp) {
     *   alert("captureImage Exception :[" + exp.code + "] " + exp.message);
     * }
     *
     * //to be invoked when the user decides to stop the camera preview
     * function stopPreview() {
     *   if(myPreviewId != null) {
     *     var preview = document.getElementById(myPreviewId);
     *     previewObject.style.visibility = "hidden"; //stop preview
     *     myParent.removeChild(preview);
     *     myPreviewId = null;
     *   } else {
     *     alert("preview already stopped");
     *   }
     * }
     */
    function createPreviewNode(successCallback, errorCallback) {
        ax.util.validateRequiredFunctionParam(successCallback);

        var node, info;
        var previewNodeID = NODE_ID_PREFIX + '_' + this.id;

        try{
            node = createNode(previewNodeID);
        }catch(e){
            this.errorAsyncWAC(e, errorCallback);
            return;
        }

        info = monitorInfo[this.id];
        info.previewNode = node;
        info.previewNodeID = previewNodeID;

        runMonitorStartPreview(this, info);
        ax.util.invokeLater(null, successCallback, node);
    }

    function runMonitorStartPreview(camera, info) {
        if (info.timerID_start) {
            window.clearInterval(info.timerID_start);
            info.timerID_start = undefined;
        }

        (function() {
            var _camera = camera, _info = info;

            _info.timerID_start = window.setInterval(function() {
                monitorStartPreview(_camera, _info);
            }, 20);
        })();
    }

    // 개발자가 preview node를 DOM에 add하고 visible로 바꾸기를 기다린다
    function monitorStartPreview(camera, info) {
        var node = info.previewNode;
        var nodeID = info.previewNodeID;
        var elem = getNode(nodeID);

        // 같은 ID로 다른 node를 add한 듯
        if (elem && elem !== node) {
            window.clearInterval(info.timerID_start);
            info.timerID_start = undefined;
            return;
        }

        // node가 DOM에 추가되었는지, visible인지 두 가지 모두 검사
        if (elem && elem.style.visibility === 'visible') {
            var x, y, w, h;

            window.clearInterval(info.timerID_start);
            info.timerID_start = undefined;

            var pos = getAbsolutePosition(node);
            x = pos.left;
            y = pos.top;
            w = node.offsetWidth;
            h = node.offsetHeight;

            camera.execAsyncWAC('startPreview',
                    function() {
                        info.previewing = true;
                        runMonitorStopPreview(camera, info);
                    },
                    function(e) {
                        if(_DEBUG) { ax.debug('failed to start preview. err = {0}', e); }
                    },
                    [camera.id, x, y, w, h]
            );
        }
    }

    function runMonitorStopPreview(camera, info) {
        if (info.timerID_stop) {
            window.clearInterval(info.timerID_stop);
            info.timerID_stop = undefined;
        }

        (function() {
            var _camera = camera, _info = info;

            _info.timerID_resize = window.setInterval(function() {
                monitorPreviewRect(_camera, _info);
            }, 500);

            _info.timerID_stop = window.setInterval(function() {
                monitorStopPreview(_camera, _info);
            }, 50);
        })();
    }

    function monitorPreviewRect(camera, info) {
        var pos = getAbsolutePosition(info.previewNode);
        var x = pos.left;
        var y = pos.top;
        var w = info.previewNode.offsetWidth;
        var h = info.previewNode.offsetHeight;

        if (info.previewLeft !== x || info.previewTop !== y || info.previewWidth !== w || info.previewHeight !== h) {
            var pos = getAbsolutePosition(info.previewNode);
            camera.execAsyncWAC('setPreviewLayout',
                                function() {
                                    info.previewLeft = x;
                                    info.previewTop = y;
                                    info.previewWidth = w;
                                    info.previewHeight = h;
                                },
                                function() {
                                    if(_DEBUG) { ax.debug('failed to setPreviewLayout'); }
                                },
                                [camera.id, x, y, w, h]
            );
        }
    }

    // 개발자가 preview node를 invisible로 바꾸거나 DOM에서 remove하기를 기다린다
    function monitorStopPreview(camera, info) {
        var node = info.previewNode;
        var nodeID = info.previewNodeID;
        var elem = getNode(nodeID);

        if (!elem || elem !== node || elem.style.visibility === 'hidden') {
            window.clearInterval(info.timerID_stop);
            info.timerID_stop = undefined;

            window.clearInterval(info.timerID_resize);
            info.timerID_resize = undefined;
            info.previewWidth   = undefined;
            info.previewHeight  = undefined;
            info.previewTop       = undefined;
            info.previewLeft      = undefined;

            try {
                camera.execSyncWAC('stopPreview', [camera.id]);
                info.previewing = false;

                if(monitorInfo[camera.id].vdoCapturing) {
                    monitorInfo[camera.id].vdoCapturing = false;
                }
            }
            catch (e) {
                if(_DEBUG) { ax.debug('failed to stop preview. err = {0}', e); }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // clear monitor & preview on unload

    window.addEventListener('unload', function() {
        var n;
        for (n in monitorInfo) {
            var info = monitorInfo[n];

            if (info.previewing) {
                try {
                    info.camera.execSyncWAC('stopPreview', [info.camera.id]);
                }
                catch (ignore) {
                }
            }

            if (undefined !== info.timerID_start) {
                window.clearInterval(info.timerID_start);
            }

            if (undefined !== info.timerID_stop) {
                window.clearInterval(info.timerID_stop);
            }

            if (undefined !== info.timerID_resize) {
                window.clearInterval(info.timerID_resize);
            }

        }
    }, false);

    // ====================================================
    Camera.prototype = ax.plugin('deviceapis.camera', {
        'captureImage': captureImage,
        'startVideoCapture': startVideoCapture,
        'stopVideoCapture': stopVideoCapture,
        'createPreviewNode': createPreviewNode
    });
    CameraManager.prototype = ax.plugin('deviceapis.camera', {
        'getCameras': getCameras,
        'stopVideoCaptureOf': stopVideoCaptureOf
    });
    ax.def(g).constant('CameraManager', CameraManager);
    ax.def(deviceapis).constant('camera', new CameraManager());
}(window));

/**
 * <p>카메라 기능에 대한 선택사항을 지정합니다. 이 인터페이스는 captureImage와 startVideoCapture 함수에서 선택사항을 지정하기 위해 사용됩니다.</p>
 * @class 카메라 기능에 대한 선택사항을 지정합니다.
 * @name CameraOptions
 */

/**
 * <p>촬영된 사진과 비디오 파일에 이름을 부여하고 파일이 저장될 경로로 응용 프로그램이 선호하는 경로를 지정할 수 있습니다. 단, 경로는 WAC 파일 시스템에서 규정한 가상 경로로서 Filesystem.resolve() 함수에서 사용 가능한 형식이어야 합니다. 다음과 같은 매개변수는 무시될 수 있습니다.<br>
 * •값이 null인 경우 <br>
 * •유효하지 않은 위치를 지정한 경로 <br>
 * •OS의 제한으로 사진이나 비디오를 저장할 수 없는 경우 <br>
 * 매개변수가 무시되는 경우 WAC 런타임은 이미지와 비디오에 대한 형식이나 경로, 확장자 등을 임의로 선정합니다. 이 경우 WAC 런타임은 임의로 지정한 실제의 경로와 파일 이름을 매개변수로 하여 CameraCaptureSuccessCallback 함수를 호출합니다.
 * 매개변수가 무시되지 않더라도 파일 확장자에 따른 미디어 형식을 지원하지 못하는 경우가 있을 수 있습니다. 이 경우 WAC 런타임은 지원 가능한 형식으로 파일을 저장하고 확장자를 변경합니다. 그리고 변경된 실제의 경로와 파일 이름을 매개변수로 CameraCaptureSuccessCallback 함수를 호출합니다.<br>
 * ※ Appspresso는 유효하지 않은 경로를 지정한 경우 단말의 기본 카메라 어플리케이션의 저장 위치에 파일을 저장하며 파일 이름은 타임스탬프(1970년 1월 1일 자정으로부터 경과한 시간)로 지정됩니다.
 * Appspresso에서 지원하는 이미지의 형식과 확장자는 Android 런타임과 iOS 런타임 모두 images/jpeg, .jpg입니다. 지원하는 비디오의 형식과 확장자는 Android 런타임의 경우 video/3gpp, .3gp이며 iOS 런타임의 경우 video/mp4, .mp4입니다.
 * </p>
 * @memberOf CameraOptions#
 * @filed
 * @type DOMString
 * @name destinationFilename
 */

/**
 * <p>사진과 동영상의 해상도를 지정합니다. 이 값이 true이면 사진과 동영상을 고해상도를 촬영하고 false이면 저해상도로 촬영합니다. 이 매개변수는 대상 플랫폼에 따라 지원되지 않을 수 있습니다. 이 경우 WAC 런타임은 이 값을 무시합니다.<br>
 * ※ 현재 버전의 Appspresso 1.0 beta에서 Android 런타임은 highRes 값에 관계없이 항상 고해상도로 이미지와 동영상을 촬영하고 녹화합니다.
 * iOS 런타임은 미리보기를 시작할 때 일단 저해상도 상태로 시작합니다. 만약 captureImage 함수나 startVideoCapture 함수를 호출할 때 options 매개변수의 highRes 속성을 true로 설정하면 그 때 비로소 고해상도 상태로 변경됩니다.<br>
 * ※ Appspresso의 iOS 런타임의 경우 highRes의 값에 따른 해상도의 변화는 다음과 같습니다. Android 런타임의 경우 단말 모델에 따라 다를 수 있습니다.<br>
 * <table cellSpacing="1" cellPadding="1" width="100%" border="1">
 * <tbody>
 *     <tr align="center">
 *         <td style="background:#efefef">model</td>
 *         <td style="background:#efefef">function</td>
 *         <td style="background:#efefef">highRes: true</td>
 *         <td style="background:#efefef">highRes: false</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan="2" align="center">iphone3GS</td>
 *         <td align="center">Video</td>
 *         <td align="center">640x480 3.5mbps</td>
 *         <td align="center">480x360 700kbps</td>
 *     </tr>
 *     <tr>
 *         <td align="center">Image</td>
 *         <td align="center">2048x1536</td>
 *         <td align="center">640x480</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan="2" align="center">iphone4<br>front camera</td>
 *         <td align="center">Video</td>
 *         <td align="center">640x480 3.5mbps</td>
 *         <td align="center">480x360 700kbps</td>
 *     </tr>
 *     <tr>
 *         <td align="center">Image</td>
 *         <td align="center">640x480</td>
 *         <td align="center">480x360</td>
 *     </tr>
 *     <tr>
 *         <td rowSpan="2" align="center">iphone4<br>back camera</td>
 *         <td align="center">Video</td>
 *         <td align="center">1280x720 10.5mbps</td>
 *         <td align="center">480x360 700kbps</td>
 *     </tr>
 *     <tr>
 *         <td align="center">Image</td>
 *         <td align="center">2592x1936</td>
 *         <td align="center">1280x720</td>
 *     </tr>
 * </tbody>
 * </table>
 * </p>
 * @memberOf CameraOptions#
 * @filed
 * @type boolean
 * @name highRes
 */



/**
 * <p>사용 가능한 카메라들의 목록을 얻기 위한 성공 콜백 함수를 정의합입니다.</p>
 * @namespace
 * @name CameraArraySuccessCallback
 */

/**
 * <p>사용 가능한 카메라 목록을 얻는 데 성공한 경우 호출됩니다.</p>
 * @function
 * @param {CameraArray} obj <p>사용 가능한 카메라 목록</p>
 * @return {void}
 * @memberOf CameraArraySuccessCallback
 * @name onsuccess
 */

/**
 * <p>사진 촬영과 동영상 녹화가 성공한 경우의 결과를 처리하기 위한 성공 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name CameraCaptureSuccessCallback
 */

/**
 * <p>이미지 혹은 비디오 촬영이 성공한 경우 호출됩니다.</p>
 * @function
 * @param {DOMString} filename <p>단 이 경로는 WAC 파일 시스템에서 규정한 가상 경로로서 Filesystem.resolve() 함수에서 사용 가능한 형식.</p>
 * @return {void}
 * @name onsuccess
 * @memberOf CameraCaptureSuccessCallback
 */

/**
 * <p>사진 촬영과 동영상 녹화가 성공한 경우의 결과를 처리하기 위한 성공 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name CameraPreviewSuccessCallback
 */

/**
 * <p>비동기적 호출이 성공한 경우 호출됩니다.</p>
 * @param {HTMLElement} domObj <p>카메라 미리보기를 보여주기 위한 DOM 객체</p>
 * @function
 * @return {void}
 * @name onsuccess
 * @memberOf CameraPreviewSuccessCallback
 */


