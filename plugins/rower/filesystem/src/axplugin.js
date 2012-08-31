////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Filesystem Module. 이 API는 단말의 파일시스템에 대한 접근을 제공합니다.
 * <p>
 * 파일시스템 API는 단말의 파일시스템 상의 몇몇 특정 위치에 대응되는 가상 루트들의 집합을 추상화하여 나타냅니다. 파일시스템 API는 가상 루트와 가상 루트 하위의 계층구조를 각기 개별적인 가상 파일시스템으로 노출하며 단말의 파일시스템 상의 다른 부분은 접근을 제한합니다.
 * 각 가상 루트에는 이름이 부여되어 있으며 가상 파일시스템 상의 각 파일과 디렉토리는 다음과 같은 형식의 절대경로 형식으로 지정됩니다. &lt;root name&gt;/&lt;path&gt;. &lt;root name&gt;은 가상 루트의 이름이고 &lt;path&gt;는 루트에 대한 파일과 디렉토리의 상대적인 경로입니다.
 * 가상 루트의 종류에는 다음과 같은 것들이 있습니다. <br>
 * • images: 이미지 파일들의 위치<br>
 * • videos: 동영상 파일들의 위치<br>
 * • music: 음악 파일들의 위치<br>
 * • documents: 문서들의 위치<br>
 * • downloads: 내려 받은 파일들의 위치<br>
 * • wgt-package: 위젯 패키지의 위치 (읽기 전용)<br>
 * • wgt-private: 위젯 전용의 저장 공간<br>
 * • wgt-private-tmp: 위젯 전용의 휘발성 저장 공간<br>
 * • removable: 교체 가능한 저장공간의 위치 (※ iOS에서는 removable을 지원하지 않습니다.)<br>
 * <br>
 * 위에서 나열한 가상 루트로부터의 특정한 경로에 접근하려면 filesystem.resolve 함수를 호출하여 파일 핸들을 반환 받아야 합니다.<br>
 * 파일 핸들은 파일과 디렉토리 모두에 대응되며 파일 핸들의 isFile 속성이 true이면 파일이고 isDirectory 속성이 true이면 디렉토리입니다. 파일을 열고 FileStream 핸들을 이용해 파일을 읽고 쓸 수 있습니다. 디렉토리는 그 하위에 있는 파일들과 디렉토리들을 나열할 수 있습니다.<br>
 * 경로 분리자로는 ‘/’ 문자가 사용되며 현재 디렉토리를 지칭하는 ‘.’과 상위 디렉토리를 지칭하는 ‘..’ 표현은 지원되지 않습니다. 경로를 구성하는 모든 문자는 UTF-8로 인코딩되어야 합니다.<br>
 * <br>
 * http://wacapps.net/api/filesystem 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 FileSystemManager 인터페이스의 인스턴스가 deviceapis. filesystem 으로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/filesystem</strong><br>
 * 이 모듈의 모든 기능 사용 가능.<br>
 * <strong>http://wacapps.net/api/filesystem.read</strong><br>
 * copyTo(), moveTo(), createDirectory(), createFile(), deleteDirectory(), deleteFile(), 그리고  "w",  "a" mode의 openStream을 제외한 모든 기능 사용<br>
 * <strong>http://wacapps.net/api/filesystem.write</strong><br>
 * readAsText(), "r" mode의 openStream을 제외한 모든 기능 사용
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

    ax.def(g)
        .constant('FileArray', Array)
        .constant('ByteArray', Array);

    function castAsString(value) {
        return (value === null || value === undefined) ? value : value + '';
    }

    ////////////////////////////////////////////////////////////////////////////
    // convenient functions, variables

    var filterDates = ['startModified', 'endModified', 'startCreated', 'endCreated'];

    function internalParams(file) {
        return {
            'fullPath': file.fullPath,
            'mode': file.mode
        };
    }

    function reviseFileFilter(filter) {
        var newFilter = {};

        if (filter) {
            newFilter.name = castAsString(filter.name);
            reviseFilterDate(newFilter, filter);
        }

        return newFilter;
    }

    function reviseFilterDate(newFilter, filter) {
        var i, pname, value;

        for (i = 0; i < filterDates.length; i++) {
            pname = filterDates[i];

            if (pname in filter) {
                value = filter[pname];

                if (value instanceof Date) {
                    newFilter[pname] = value.getTime();
                }
                else {
                    try {
                        newFilter[pname] = new Date(value).getTime();
                    }
                    catch (e) {
                        newFilter[pname] = -1;
                    }
                }
            }
            else {
                newFilter[pname] = -1;
            }
        }
    }


    function convertToValidPath(path) {

        if( !ax.util.isValidPath(path) ){
            throw new DeviceAPIError(ax.INVALID_VALUES_ERR);
        }

        if (path.charAt(path.length - 1) === '/') {
            path = path.substring(0, path.length - 1);
        }
        if (path.charAt(0) === '/') {
            path = path.substring(1);
        }

        return path;
    }

    ////////////////////////////////////////////////////////////////////////////
    // constructor FileSystemManager

    var FILE_SYSTEM_MANAGER = 'deviceapis.filesystem';
    var FILE_SYSTEM = 'filesystem';
    var CONSTRUCTOR_FILE_SYSTEM_MANAGER = 'FileSystemManager';


    /**
     * 파일시스템 모듈의 관리자 객체를 정의합니다. 이 관리자 인터페이스는 경로를 지정하면 파일 핸들을 반환하는 파일시스템의 기본 API를 제공합니다.
     *
     * @class 파일시스템 모듈의 관리자 객체를 정의합니다.
     * @name FilesystemManager
     * @example
     * function successCB(files) {
     *   for(var i = 0; files.length; i++) {
     *     alert("File Name is " + files[i].name); // displays file name
     *   }
     *
     * function successCB(files) {
     *    for(var i = 0; files.length; i++) {
     *       alert("File Name is " + files[i].name); // displays file name
     *    }
     *
     *    var testFile = documentsDir.createFile("test.txt");
     *
     *    if (testFile != null) {
     *       file.open(
     *             function(fs){
     *                 fs.write("HelloWorld");
     *                 fs.close();
     *             }, function(e){
     *                 alert("Error " + e.message);
     *                 }, "w", "UTF-8"
     *             );
     *    }
     * }
     *
     * function errorCB(error) {
     *    alert("The error " + error.message + " occurred when listing the files in the selected folder");
     * }
     *
     * var documentsDir;
     * deviceapis.filesystem.resolve(
     *       function(dir){
     *          documentsDir = dir;
     *          dir.listFiles(successCB,errorCB);
     *       }, function(e){
     *          alert("Error" + e.message);
     *          }, 'documents',  "rw"
     *       );
     *
     */

    function FileSystemManager() {
    }

    // properties/methods of FileSystemManager

    /**
     * 단말 플랫폼에 따른 경로의 최대길이로 읽기 전용의 속성입니다.
     *
     * @filed
     * @name maxPathLength
     * @memberOf FilesystemManager.prototype
     * @type long
     * @example
     * alert("The maximum path length is " + deviceapis.filesystem.maxPathLength);
     *
     */
    var MAX_PATH_LENGTH = 'maxPathLength';
    var RESOLVE = 'resolve';

    /**
     * 파일 경로를 지정받아 파일 핸들로 반환합니다.<br>지정된 경로의 유효성 검사를 통해 파일 핸들을 생성합니다. 파일 핸들의 생성에 성공하면 파일 핸들이 매개변수로 successCallback 함수가 호출됩니다. 경로는 반드시 가상 루트의 이름으로 시작해서 존재하며 접근 가능한 파일 혹은 디렉토리를 지정해야 합니다.
     * <p>
     * - documents: 단말에서 pdf, doc 등의 텍스트 문서가 저장되는 기본 폴더이며 어떤 플랫폼에서는 “My Documents” 폴더가 이에 해당합니다.<br>
     * - images: 단말에서 jpg, gif, png 등의 이미지 파일이 저장되는 기본 폴더이며 어떤 플랫폼에서는 “My Images” 폴더가 이에 해당합니다.<br>
     * - music: 단말에서 mp3, aac 등의 사운드 파일이 저장되는 기본 폴더이며 어떤 플랫폼에서는 “My Music” 폴더가 이에 해당합니다.<br>
     * - videos: 단말에서 avi, mp3 등의 동영상 파일이 저장되는 기본 폴더이며 어떤 플랫폼에서는 “My Videos” 폴더가 이에 해당합니다.<br>
     * - downloads: 단말에서 브라우저나 메일 클라이언트를 통해 내려 받은 파일이 저장되는 기본 폴더이며 어떤 플랫폼에서는 “Downloads” 폴더가 이에 해당합니다.<br>
     * - wgt-package: 위젯 파일의 압축이 풀린 읽기 전용의 폴더입니다.<br>
     * - wgt-private: 위젯 전용의 정보 저장을 위한 폴더입니다. 이 폴더는 오직 해당 위젯만이 접근할 수 있으며 다른 위젯이나 어플리케이션은 접근할 수 없습니다.<br>
     * - wgt-private-tmp: 위젯이 실행 중인 동안 데이터를 저장할 수 있는 위젯 전용 폴더입니다. 위젯이 종료되거나 웹 런타임이 재시작하면 이 폴더에 저장된 파일과 디렉토리는 삭제됩니다. 이 폴더는 오직 해당 위젯만이 접근할 수 있으며 다른 위젯이나 어플리케이션은 접근할 수 없습니다.<br>
     * - removable: SD카드와 같이 교체 가능한 미디어에 대응되는 폴더입니다.<br>
     * ※ 현재 버전의 Appspresso 1.0 beta에서 지원하는 iOS와 Android에는 “My Documents”와 같이 application들이 특정한 종류의 파일을 저장하고 공유하기 위한 폴더가 존재하지 않습니다. Appspresso의 iOS 런타임과 Android 런타임은 documents, images, music, videos, downloads 등의 가상 루트를 해당 application만이 접근 가능한 위치에 있는 디렉토리와 대응시킵니다.<br>
     * 매개변수 mode는 반환된 파일 객체가 지정된 경로를 포함하는 가상 루트와 그 하위의 전체 트리에 대해 읽기 권한을 가질지 읽고 쓰기 권한을 가질지를 지정합니다. 요청된 권한은 보안 프레임워크로부터 획득됩니다. 한 번 파일 객체가 권한을 획득하면 이 권한은 이 파일로부터 파생되는 모든 파일들에 보안 프레임워크의 참조 없이 상속됩니다.<br>
     * ※ 이 함수에 대한 Waikiki API의 명세는 다소 혼란스러운 면이 있습니다. Appspresso의 런타임은 가상 루트로 시작하는 모든 경로에 대해 resolve 함수를 호출하면 파일 핸들을 반환합니다. 그러나 명세에 대한 오해에서 비롯된 동작 방식일 수 있으므로 오직 가상 루트만을 경로로 지정하고 가상 루트 하위의 파일 혹은 디렉토리는 경로로 지정하지 않을 것을 권고합니다.<br>
     * 지정된 경로를 파일 핸들로 변환할 수 없는 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * - IO_ERR: 유효하지 않은 경로를 지정한 경우<br>
     * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
     * - SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * </p>
     *
     * @function
     * @name resolve
     * @memberOf FilesystemManager.prototype
     *
     * @param {FileSystemSuccessCallback} successCallback <p>지정된 경로를 파일 핸들로 변환하는 데 성공한 경우 콜백으로 호출됩니다.</p>
     * @param {FileSystemErrorCallBack} errorCallback <p>오류가 발생한 경우 콜백으로 호출됩니다.</p>
     * @param {DOMString} location <p>파일 핸들로 변환할 경로. 가상 루트로 시작하는 유효한 경로</p>
     * @param {DOMString} mode <p>successCallback 함수로 전달되는 파일 핸들에서 접근 가능한 모든 파일과 디렉토리에 대해 ‘r’로 지정한 경우 읽기 권한을, ‘rw’로 지정한 경우 읽고 쓰기 권한을 획득합니다. 이 매개변수는 생략 가능하며 생략된 경우 ‘rw’ 값이 적용됩니다.</p>
     * @type long
     * @returns {long} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * deviceapis.filesystem.resolve(
     *   function(dir) {
     *     alert("Mount point Name is " +  dir.name);
     *   }, function(e) {
     *     alert(e.message);
     *   }, 'images', 'r'
     * );
     *
     */
    function resolve(successCallback, errorCallback, location, mode) {
        ax.util.validateRequiredFunctionParam(successCallback);        // execAsyncWAC 호출 전 scb 를 감싸는 경우 체크.....
        ax.util.validateRequiredStringParam(location);
        ax.util.validateOptionalStringParam(mode);

        try {
            if (mode == null || mode == undefined) mode = 'rw';
            if (mode !== 'r' && mode !== 'rw') {
                this.errorAsyncWAC(ax.INVALID_VALUES_ERR, errorCallback);
                return;
            }

            location = convertToValidPath(location);
        } catch(e) {
            this.errorAsyncWAC(e, errorCallback);
            return;
        }

        function scb(result) {
            var file = new File(result);
            ax.util.invokeLater(null, successCallback, file);
        }

        return this.execAsyncWAC(RESOLVE, scb, errorCallback, [location, mode]);
    }

    ////////////////////////////////////////////////////////////////////////////
    // constructor File

    var PARENT = 'parent';
    var READONLY = 'readOnly';
    var CREATED = 'created';
    var MODIFIED = 'modified';
    var FILESIZE = 'fileSize';
    var LENGTH = 'length';
    var TO_URI = 'toURI';
    var LIST_FILES = 'listFiles';
    var OPEN_STREAM = 'openStream';
    var READ_AS_TEXT = 'readAsText';
    var COPY_TO = 'copyTo';
    var MOVE_TO = 'moveTo';
    var CREATE_DIRECTORY = 'createDirectory';
    var CREATE_FILE = 'createFile';
    var RESOLVE_FILE_PATH = 'resolve';
    var DELETE_DIRECTORY = 'deleteDirectory';
    var DELETE_FILE = 'deleteFile';

    /**
     *
     * <p>이 인터페이스는 파일을 추상화하여 표현합니다. 파일 핸들은 파일 혹은 디렉토리를 표현합니다. 만약 isFile 속성의 값이 true이면 파일을 표현하고 그렇지 않으면 디렉토리를 표현합니다.<br>
     * 파일 핸들은 resolve 함수에서 지정한 mode 매개변수에 따라 자신과 자신의 하위 계층구조에 대해 읽기 그리고(혹은) 쓰기 권한을 갖습니다. 파일 핸들을 통해 다른 파일을 생성할 경우 새로이 생성되는 파일은 보안 프레임워크의 참조없이  파일 핸들의 접근 권한을 상속받습니다.<br>
     * 파일 핸들이 파일인 경우 읽기와 쓰기 같은 I/O 연산을 위해 열 수 있습니다. 파일 핸들이 디렉토리인 경우 하위에 있는 모든 파일과 디렉토리를 나열하는 데 사용될 수 있습니다.<br>
     * </p>
     *
     * @class 이 인터페이스는 파일을 추상화하여 표현합니다.
     * @name File
     *
     * @example
     * function successCB(files) {
     *   for(var i = 0; i < files.length; i++) {
     *     // alerts each name of dir's contents
     *     alert(files[i].name);
     *   }
     * }
      *
     * function errorCB(error) {
     *   alert("The error " + error.message + "
     *         occurred when listing the files in the selected folder");
     * }
      *
     * // list directory contents
     * dir.listFiles(successCB, errorCB);
     *
     */
    function File() {
        var peer = arguments[0];
        var self = this;

        peer.created = peer.created ? new Date(peer.created) : new Date(0);
        peer.modified = peer.modified ? new Date(peer.modified) : new Date(0);

        /**
         * <p>부모 디렉토리의 핸들. 부모 디렉토리가 없는 경우 null이며 이는 이 파일이 루트임을 의미합니다. 이 속성은 읽기전용입니다.</p>
         *
         * @field
         * @type File
         * @name parent
         * @memberOf File#
         *
         * @example
         * // list directory contents
         * dir.listFiles(successCB, errorCB);
         *
         * function successCB(files) {
         *   for(var i = 0; i < files.length; i++) {
         *     // alerts the file parent, should contain the
         *     // same value for all the files in the loop
         *     alert("All the files should have the same parent " +
         *           files[i].parent);
         *   }
         * }
         *
         * function errorCB(error){
         *   alert("The error " + error.message +
         *         " occurred when listing the files in the selected folder");
         * }
         *
         */
        function getParent() {
            var parentPeer = self.execSyncWAC('getParent', [internalParams(peer)]);
            return !parentPeer ? null : new File(parentPeer);
        }

        /**
         * <p>파일시스템 상의 파일 / 디렉토리에 대한 접근 상태.<br>파일이 쓰기 가능한 상태이면 false이고 읽기전용인 경우 true입니다. 이 속성은 파일시스템 상의 파일과 디렉토리의 실제 상태를 나타냅니다. 이 값은 FileSystemManager.resolve 함수에 매개변수로 지정한 mode 매개변수에 영향을 받지 않습니다.<br>이 속성은 읽기전용입니다.</p>
         *
         * @field
         * @type boolean
         * @memberOf File#
         * @name readOnly
         *
         * @example
         * // list directory contents
         * dir.listFiles(successCB, errorCB);
         *
         * function successCB(files) {
         *   for(var i = 0; i < files.length; i++) {
         *     if(files[i].readOnly)
         *       alert("Cannot write to file " + files[i].name);
         *     else
         *       alert("Can write to file " + files[i].name);
         *   }
         * }
         *
         * function errorCB(error) {
         *   alert("The error " + error.message + " occurred when listing the files in the selected folder");
         * }
         */
        function getReadOnly(){
            var result = self.execSyncWAC('getReadOnly', [internalParams(peer)]);
            return ax.isBoolean(result) ? result : undefined;
        }

        /**
         * <p>파일 종류. 파일의 경우 true, 디렉토리의 경우 false입니다. 이 속성은 읽기전용입니다.</p>
         * @field
         * @type boolean
         * @name isFile
         * @memberOf File#
         */

        /**
         * <p>파일 종류. 디렉토리의 경우 true, 파일의 경우 false입니다. 이 속성은 읽기전용입니다.</p>
         * @field
         * @type boolean
         * @name isDirectory
         * @memberOf File#
         */

        /**
         * <p>파일이 생성된 시간(타임 스탬프).<br> 파일이 파일시스템 상에 처음 생성되었을 때의 시간으로 createFile 함수가 성공했을 때의 시간과 동일합니다. 만약 파일이 이동될 경우 생성 시간이 변경되어야 하는지 여부는 명세에서 명시하지 않으며 플랫폼 특성에 따릅니다.<br> 이 속성은 읽기전용입니다. </p>
         * <p>※ 현재 Android의 파일시스템은 파일 생성 시간을 지원하지 않습니다. 따라서 Appspresso의 Android 런타임에서 이 값은 undefined로 지정됩니다</p>
         * @field
         * @type Date
         * @name created
         * @memberOf File#
         */
        function getCreated(){
            var result = self.execSyncWAC('getCreated', [internalParams(peer)]);
            return ax.isNumber(result) ? result : undefined;
        }

        /**
         * <p>파일이 수정된 시간(타임 스탬프).<br>파일이 파일 시스템 상에서 가장 최근에 수정된 시간으로 대개 마지막으로 파일에 쓰기 연산이 이루어진 시간입니다. 읽기 위해 파일을 연 경우에는 이 시간이 변경되지 않습니다.<br>이 속성은 읽기전용입니다. <br>※ Appspresso 런타임에서 가상 루트 wgt-package의 하위에 있는 파일과 디렉토리는 이 속성 값이 의미 없는 값으로 지정됩니다. </p>
         * @field
         * @type Date
         * @name modified
         * @memberOf File#
         * @example
         * alert(file.modified); // displays the modification timestamp
         */
        function getModified(){
            var result = self.execSyncWAC('getModified', [internalParams(peer)]);
            return ax.isNumber(result) ? result : undefined;
        }

        /**
         * <p>파일 이름을 제외한 파일의 경로입니다. <br> 이 파일 핸들을 포함하는 루트의 이름으로 시작하여 이 파일을 포함하고 있는 디렉토리까지의 경로입니다. 파일 핸들이 루트 자신인 특수한 경우를 제외하고 이 값의 마지막 문자는 항상 ‘/’입니다.<br>
         * 예를 들어 파일이 music/ramones/volume1/RockawayBeach.mp3인 경우 path는 music/ramones/volume1/ 입니다. 디렉토리의 경우 경로가 music/ramones/volume1 이라면 path는 music/ramones/입니다.<br>
         * 특수한 경우로 루트 자신인 경우 루트가 music 이라면 path 역시 music입니다.<br>
         * 이 속성은 읽기전용입니다
         * </p>
         * @field
         * @type DOMString
         * @name path
         * @memberOf File#
         * @example
         * alert(file.path); // should be 'music/' if the file is music/foo.mp3
         */

        /**
         * <p>파일의 이름으로 경로를 구성하는 다른 요소는 모두 제외됩니다. <br>루트의 이름과 다른 모든 path 구성 요소를 제외한 파일의 이름입니다.<br>
         * 예를 들어 파일이 music/ramones/volume1/RockawayBeach.mp3인 경우 name은 RockawayBeach.mp3입니다. 디렉토리의 경우 경로가 music/ramones/volume1이라면 name은 volume1입니다.<br>
         * 특수한 경우로 루트 자신인 경우 name은 빈 문자열입니다.<br>
         * 이 속성은 읽기전용입니다</p>
         * @field
         * @type DOMString
         * @name name
         * @memberOf File#
         * @example
         * alert(file.name); // should be foo.mp3 if the file path is music/foo.mp3
         */

        /**
         * <p>파일의 전체 경로. <br>이 파일의 전체 경로로 루트의 이름으로 시작해서 파일 혹은 디렉토리 자신의 이름까지를 포함합니다.<br>
         * 예를 들어 파일이 music/ramones/volume1/RockawayBeach.mp3인 경우 path는 music/ramones/volume1/ RockawayBeach.mp3 입니다. 디렉토리의 경우 music/ramones/volume1이라면 music/ramones/ volume1입니다.<br>
         * 특수한 경우로 루트 그 자신인 경우 가령 루트가 music이라면 fullPath는 music입니다.<br>
         * fullPath는 항상 path + name과 일치합니다.<br>
         * 이 속성은 읽기전용입니다</p>
         * @field
         * @type DOMString
         * @name fullPath
         * @memberOf File#
         * @example
         * alert(file.fullPath); // should be music/track1.mp3 if the file is music/track1.mp3
         */

        /**
         * <p>바이트 단위의 파일 크기.디렉토리에 대해 이 속성을 읽으면 undefined가 반환됩니다. 디렉토리의 하위에 있는 파일과 디렉토리의 수를 얻으려면 length 속성을 사용해야 합니다.<br>
         * 이 속성은 읽기전용입니다<br>
         * ※ Appspresso의 현재 버전 1.0 beta에서 Android 런타임은 가상 루트 wgt-package 하위에 있는 파일에 대해 이 속성의 값을 실제의 값보다 작은 값으로 보고할 수 있습니다. 만약 파일의 모든 바이트를 읽고자 한다면 이 값을 참조하기 보다는 FileStream 인터페이스의 eof 속성을 참조해야 합니다</p>
         * @field
         * @type long
         * @name fileSize
         * @memberOf File#
         * @example
         * alert(file.fileSize); // displays the file size
         */
        function getFileSize(){
            if( peer.isDirectory ) return;

            var result = self.execSyncWAC('getFileSize', [internalParams(peer)]);
            return ax.isNumber(result) ? result : undefined;
        }

        /**
         * <p>이 파일 핸들이 포함하고 있는 파일과 디렉토리들의 수.<br>
         * 파일에 대해 이 속성을 읽으면 undefined가 반환됩니다. 파일의 크기를 얻으려면 fileSize 속성을 사용해야 합니다.<br>
         * 이 속성은 읽기전용입니다
         * </p>
         * @field
         * @type long
         * @name length
         * @memberOf File#
         * @example
         * alert(file.length); // '3' if the directory contains two files and one sub-directory
         */
        function getLength(){
            if( peer.isFile ) return;

            var result = self.execSyncWAC('getLength', [internalParams(peer)]);
            return ax.isNumber(result) ? result : undefined;
        }

        /**
         * 파일의 URI를 반환합니다. <br>
         * 이 파일을 식별할 수 있는 URI를 반환합니다. 이 URI는 만료시기가 없으며 파일이 존재하는 한 유효합니다. URI는 WAC 런타임에 의해 생성되며 해당 위젯과 파일에 대해 유일합니다. 다른 위젯에서 URI를 토대로 실제의 파일 경로를 유추해 내는 것을 어렵게 하기 위해 WAC 런타임은 가능하면 URI를 알아보기 어렵게 만듭니다. URI는 보안 상의 이유로 가급적 실제의 경로를 포함하지 않으나 파일 이름은 포함될 수 있습니다.
         *
         * @memberOf File#
         * @type DOMString
         * @returns {DOMString} <p>파일을 식별할 수 있는 URI. 오류가 발생한 경우 null</p>
         * @exception <p>- NOT_SUPPORTED_ERR: 이 연산이 지원되지 않는 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         * @example
         * // 'file://A123456ABCD/RockawayBeach.mp3' if the file is
         * // music/ramones/RockawayBeach.mp3
         * alert(file.toURI());
         *
         */
        function toURI() {
            return self.execSyncWAC(TO_URI, [internalParams(peer)]);
        }

        /**
         *
         * <p>이 디렉토리의 하위 파일들의 목록을 반환합니다.<br> 하위 파일들의 목록을 포함하는 FileArray 객체를 매개변수로 successCallback 함수가 호출됩니다. 단, 현재 디렉토리를 나타내는 “.”과 상위 디렉토리를 나타내는 “..”은 포함되지 않습니다. 배열에 포함된 파일들은 이 함수가 호출된 파일 객체의 접근 권한을 상속 받습니다.
         * filter 매개변수를 지정한 경우 FileFilter 인터페이스에 지정한 조건과 부합하는 파일들과 디렉토리들만이 successCallback으로 전달되며 Filter를 지정하지 않은 경우 전체 파일들과 디렉토리들이 successCallback으로 전달됩니다. 만약 디렉토리가 비어 있거나 filter 조건에 맞는 파일 혹은 디렉토리가 없다면 빈 배열이 successCallback에 전달됩니다.<br>
         * 하위 파일들의 목록을 전달할 수 없는 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
         * - IO_ERR: 이 연산이 디렉토리가 아닌 파일에서 호출된 경우<br>
         * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * - SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * - INVALID_VALUES_ERR: 입력된 매개변수가 유효한 값이 아닌 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {FileSystemSuccessCallback} successCallback <p>하위 파일들의 목록을 얻는 데 성공한 경우 호출됩니다.</p>
         * @param {ErrorCallBack} errorCallback <p>오류가 발생한 경우 호출됩니다.</p>
         * @param {FileFilter} filter <p>목록에 포함될 파일의 조건을 지정합니다.</p>
         * @memberOf File#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         * @example
         * function successCB(files) {
         *   alert("There are " + files.length  + " in the selected folder");
         * }
         *
         * function errorCB(error) {
         *   alert("The error " + error.message + " occurred when listing the files in the selected folder");
         * }
         *
         * deviceapis.filesystem.resolve(
         *     function(dir){
         *       dir.listFiles(successCB, errorCB)
         *     }, function(e){
         *       alert("Error " + e.message);
         *     }, "documents", "r"
         * );
         *
         */
        function listFiles(successCallback, errorCallback, filter) {
            ax.util.validateOptionalObjectParam(filter);

            function scb(result) {
                var i, files = [];

                if (result && result instanceof Array) {
                    for (i = 0; i < result.length; i++) {
                        files[i] = new File(result[i]);
                    }
                }

                ax.util.invokeLater(null, successCallback, files);
            }

            return this.execAsyncWAC(LIST_FILES, scb, errorCallback, [internalParams(peer), reviseFileFilter(filter)]);
        }

        /**
         * <p>지정된 접근 권한과 인코딩에 따라 파일을 엽니다.<br>이 함수는 비동기적으로 수행되며 파일 열기에 성공한 경우 지정된 접근 권한에 따라 파일을 읽고 쓸 수 있는 FileStream 객체를 매개변수로 successCallback 함수가 호출됩니다.<br>
         * 매개변수 encoding에 지정할 수 있는 지원가능한 인코딩은 “UTF-8”과 “ISO-8859-1” latin1 인코딩입니다.<br>
         * ※ Appspresso는 “EUC-KR” 인코딩을 추가 지원합니다.<br>
         * 파일을 열 수 없는 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.</p>
         * <p>
         * - INVALID_VALUES_ERR : 입력 매개변수가 유효한 값을 가지고 있지 않음. 예를 들어 mode가 "a', "r", "w"가 아님. <br>
         * - IO_ERR: 디렉토리에서 이 함수를 호출하거나 파일이 존재하지 않는 등 파일이 유효하지 않은 경우<br>
         * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * - SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {FileSystemSuccessCallback} successCallback <p>파일 열기에 성공한 경우 호출됩니다.</p>
         * @param {ErrorCallBack} errorCallback <p>파일 열기에 실패한 경우 호출됩니다.(에러를 무시할 경우 null도 가능)</p>
         * @param {DOMString} mode <p>파일 열기 모드</p>
         * @param {DOMString} encoding <p>파일 읽기와 쓰기를 위한 인코딩</p>
         * @memberOf File#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * function successCB(files) {
         *   for(var i = 0; files.length; i++) {
         *     alert("File Name is " + files[i].name); // displays file name
         *   }
         *
         *   var testFile = documentsDir.createFile("test.txt");
         *   if (testFile != null) {
         *     file.openStream(
         *         function(fs){
         *           fs.write("HelloWorld");
         *           fs.close();
         *         }, function(e){
         *           alert("Error " + e.message);
         *         }, "w", "UTF-8"
         *     );
         *   }
         * }
         *
         * function errorCB(error) {
         *   alert("The error " + error.message + " occurred when listing the files in the selected folder");
         * }
         *
         * var documentsDir;
         * deviceapis.filesystem.resolve(
         *     function(dir){
         *       documentsDir = dir; dir.listFiles(successCB,errorCB);
         *     }, function(e) {
         *       alert("Error" + e.message);
         *     }, 'documents', "rw"
         * );
         *
         */
        function openStream(successCallback, errorCallback, mode, encoding) {
            ax.util.validateRequiredStringParam(mode);
            ax.util.validateOptionalStringParam(encoding);

            if(!encoding) encoding = 'UTF-8';

            if (mode !== 'r' && mode !== 'a' && mode !== 'w') {
                this.errorAsyncWAC(ax.INVALID_VALUES_ERR, errorCallback);
                return;
            }

            function scb(result) {
                ax.util.invokeLater(null, successCallback, new FileStream(result));
            }

            return self.execAsyncWAC(OPEN_STREAM, scb, errorCallback, [internalParams(peer), mode + '', encoding + '']);
        }


        /**
         * <p>파일의 내용을 문자열로 읽습니다.<br>
         * 이 함수가 호출되면 지정된 인코딩 형식으로 파일의 내용을 읽고 이 문자열을 매개변수로 successCallback 함수를 호출합니다. 지원되는 인코딩은 “UTF-8”과 “ISO-8859-1”입니다. “UTF-8”이 기본 인코딩이며 오류가 발생한 경우 “ISO-8859-1” latin1 인코딩이 적용됩니다.<br>
         * ※ encoding 매개변수는 생략가능한 선택적 매개변수가 아니므로 기본 인코딩을 적용할 수 없습니다. Appspresso는 항상 매개변수로 지정한 인코딩을 따르며 “EUC-KR” 인코딩을 추가 지원합니다.<br>
         * 이 연산이 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.</p>
         * <p>
         * - INVALID_VALUES_ERR : 입력 매개변수가 유효한 값을 가지고 있지 않음.
         * - IO_ERR: 디렉토리에서 이 함수를 호출하거나 파일이 존재하지 않는 등 파일이 유효하지 않은 경우<br>
         * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * - SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {FileSystemSuccessCallback} successCallback <p>파일 읽기에 성공한 경우 호출됩니다.</p>
         * @param {ErrorCallBack} errorCallback <p>파일 읽기에 실패한 경우 호출됩니다.(에러를 무시할 경우 null도 가능)</p>
         * @param {DOMString} encoding <p>파일의 읽기와 쓰기 연산을 위한 인코딩.</p>
         * @memberOf File#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * function successCB(files) {
         *   for(var i = 0; files.length; i++) {
         *     alert("File Name is " + files[i].name); // displays file name
         *     if (files[i].isDirectory == false)
         *       files[i].readAsText(
         *           function(str){
         *             alert("The file content " + str);
         *           }, function(e){
         *             alert("Error " + e.message);
         *           }, "UTF-8"
         *       );
         *   }
         * }
         *
         * function errorCB(error) {
         *   alert("The error " + error.message + " occurred when listing the files in the selected folder");
         * }
         *
         * var documentsDir;
         * deviceapis.filesystem.resolve(
         *     function(dir){
         *       documentsDir = dir;
         *       dir.listFiles(successCB,errorCB);
         *     }, function(e) {
         *       alert("Error" + e.message);
         *     }, 'documents', "rw"
         * );
         *
         */
        function readAsText(successCallback, errorCallback, encoding) {
            ax.util.validateRequiredStringParam(encoding);
            encoding = encoding.toUpperCase();

            return self.execAsyncWAC(READ_AS_TEXT, successCallback, errorCallback, [internalParams(peer), encoding + '']);
        }

        /**
         * <p>지정한 경로에서 다른 경로로 파일을 복사합니다. <br> originFilePath 매개변수로 지정한 파일의 복사본이 destinationFilePath 매개변수로 지정한 경로에 생성됩니다. 복사될 파일은 반드시 이 함수를 호출하는 디렉토리의 하위에 있어야 하며 그렇지 않은 경우 복사는 수행되지 않습니다.<br>
         * 파일 복사에 실패한 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.</p>
         * <p>
         * - INVALID_VALUES_ERR: 입력 매개변수가 유효한 값을 가지고 있지 않는 경우<br>
         *   예) successCallback이 null인 경우. <br>
         * - NOT_FOUND_ERR: originFilePath가 유효한 경로가 아니거나 destinationFilePath가 유효한 경로가 아닐 경우<br>
         * - IO_ERR: 디렉토리가 아닌 파일에서 이 함수를 호출한 경우, originFilePath가 다른 프로세스에서 사용 중인 경우<br>
         *   destinationFilePath 경로에 대응하는 파일 혹은 디렉토리가 이미 존재하고 매개변수 overwrite가 false인 경우.<br>
         *   이미 존재하는 파일이고 overwrite가 true일 경우, originFilePath나 destinationFilePath에 해당하는 파일이 디렉토리 일 경우<br>
         * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * - SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우
         * </p>
         *
         * @param {FileSystemSuccessCallback} successCallback <p>파일 복사에 성공한 경우 호출됩니다.</p>
         * @param {ErrorCallBack} errorCallback <p>파일 복사에 실패한 경우 호출됩니다. (에러를 무시할 경우 null도 가능)</p>
         * @param {DOMString} originFilePath <p>원본 파일의 가상 절대 경로, 반드시 함수가 호출되는 디렉토리의 하위에 존재해야 합니다.</p>
         * @param {DOMString} destinationFilePath <p>복사본이 저장될 가상 절대 경로로 파일 이름은 [a-Z0-9_- /]+ 만이 허용됩니다.</p>
         * @param {boolean} overwrite <p>파일이 존재할 경우 덮어 쓰기 위해 true로 설정합니다.</p>
         * @memberOf File#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * file.copyTo(
         *     function(copiedFile) {
         *       alert("file copied");
         *     }, null, "images/oldpicture.jpg", "images/newpicture.jpg", false
         * );
         *
         */
        function copyTo(successCallback, errorCallback, originFilePath, destinationFilePath, overwrite) {
            ax.util.validateRequiredStringParam(originFilePath);
            ax.util.validateRequiredStringParam(destinationFilePath);
            ax.util.validateRequiredBooleanParam(overwrite);

            if( !peer.isDirectory ) {
                this.errorAsyncWAC(ax.IO_ERR, errorCallback);
                return;
            }

            if( peer.path != originFilePath.substring(0, peer.path.length) ){
                this.errorAsyncWAC(ax.NOT_FOUND_ERR, errorCallback);
                return;
            }

            if( !ax.util.isValidPath( originFilePath,destinationFilePath ) ){
                this.errorAsyncWAC(ax.INVALID_VALUES_ERR, errorCallback);
                return;
            }

            return self.execAsyncWAC(COPY_TO, successCallback, errorCallback, [internalParams(peer), originFilePath + '', destinationFilePath + '', overwrite]);
        }

        /**
         * <p>파일을 이동시킵니다.<br>
         * originFilePath 매개변수로 지정한 파일을 destinationFilePath 매개변수로 지정한 경로로 이동시킵니다. 이 연산은 어떤 플랫폼에서는 추가적인 디스크 여유 공간을 필요로 하지 않기 때문에 copyTo 함수로 파일을 복사한 후 원본 파일을 삭제하는 것과는 다릅니다. 이동시킬 파일은 반드시 이 함수를 호출하는 디렉토리 하위에 존재해야 하며 그렇지 않은 경우 연산 수행되지 않습니다.<br>
         * 파일 이동에 성공한 경우 SuccessCallback 함수가 호출되며 실패한 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.</p>
         * <p>
         * - INVALID_VALUES_ERR: 입력 매개변수가 유효한 값을 가지고 있지 않는 경우<br>
         *   예) successCallback이 null인 경우. <br>
         * - IO_ERR: 디렉토리가 아닌 파일에서 이 함수를 호출한 경우, originFilePath가 다른 프로세스에서 사용 중인 경우,<br>
         *   destinationFilePath 경로에 대응하는 파일 혹은 디렉토리가 이미 존재하고 매개변수 overwrite가 false인 경우<br>
         *   이미 존재하는 파일이고 overwrite가 true일 경우, originFilePath나 destinationFilePath에 해당하는 파일이 디렉토리 일 경우<br>
         * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * - SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * - UNKNOWN_ERR:그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {FileSystemSuccessCallback} successCallback <p>파일이 이동에 성공한 경우 콜백으로 호출됩니다.</p>
         * @param {ErrorCallBack} errorCallback <p>파일이 이동에 실패한 경우 콜백으로 호출됩니다.(에러를 무시할 경우 null도 가능)</p>
         * @param {DOMString} originFilePath <p>원본 파일의 가상 절대 경로, 반드시 현재 디렉토리의 하위에 존재해야 합니다.</p>
         * @param {DOMString} destinationFilePath <p>새 파일이 저장될 가상 절대 경로로 파일 이름은 [a-Z0-9_- /]+ 만이 허용됩니다.</p>
         * @param {boolean} overwrite <p>파일이 존재할 경우 덮어 쓰기 위해 true로 설정합니다.</p>
         * @memberOf File#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         *  file.moveTo(
         *      function(movedFile) {
         *        alert("file moved");
         *      }, null, "images/oldpicture.jpg", "images/newpicture.jpg", false
         *  );
         *
         */
        function moveTo(successCallback, errorCallback, originFilePath, destinationFilePath, overwrite) {
            ax.util.validateRequiredStringParam(originFilePath);
            ax.util.validateRequiredStringParam(destinationFilePath);
            ax.util.validateRequiredBooleanParam(overwrite);

            if( !peer.isDirectory ) {
                this.errorAsyncWAC(ax.IO_ERR, errorCallback);
                return;
            }

            if( peer.path != originFilePath.substring(0, peer.path.length) ){
                this.errorAsyncWAC(ax.NOT_FOUND_ERR, errorCallback);
                return;
            }

            if( !ax.util.isValidPath( originFilePath, destinationFilePath ) ){
                this.errorAsyncWAC(ax.INVALID_VALUES_ERR, errorCallback);
                return;
            }

            return self.execAsyncWAC(MOVE_TO, successCallback, errorCallback, [internalParams(peer), originFilePath + '', destinationFilePath + '', overwrite]);
        }

        /**
         * <p>디렉토리를 생성합니다.<br>현재 디렉토리의 상대 위치에 새로운 디렉토리를 생성합니다. 이 함수는 필요한 모든 하위 디렉토리도 함께 생성합니다. 경로에 현재 디렉토리를 나타내는 “.”과 상위 디렉토리를 나타내는 “..”의 사용은 허용되지 않습니다. 디렉토리 생성에 성공하면 생성된 디렉토리를 파일 핸들로 변환하여 반환합니다.<br>
         * 디렉토리 생성에 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체가 던져집니다.<br>
         * </p>
         *
         * @param {DOMString} dirPath <p>생성할 디렉토리의 상대 경로입니다. 경로에는 단말의 파일시스템에서 지원하는 문자만 포함되어야 합니다.</p>
         * @memberOf File#
         * @type File
         * @returns {File} <p>새로 생성된 디렉토리의 파일 핸들입니다. 반환된 File 객체는 createDirectory 함수를 호출한 File객체로부터 접근권한 “rw”를 상속받습니다.</p>
         * @exception
         * <p>
         * - INVALID_VALUES_ERR: dirPath가 유효한 경로가 아닌 경우<br>
         * - TYPE_MISMATCH_ERR: 매개변수의 형식이 올바르지 않은 경우<br>
         * - IO_ERR: dirPath의 디렉토리(파일)가 이미 존재할 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @example
         * var newDir = dir.createDirectory("newDir");
         * var anotherNewDir = dir.createDirectory("newDir1/subNewDir1");
         *
         */
        function createDirectory(dirPath) {
            ax.util.validateRequiredStringParam(dirPath);

            if( !peer.isDirectory ) {
                throw new DeviceAPIError(ax.IO_ERR);
            }

            dirPath = convertToValidPath(dirPath);

            var result = self.execSyncWAC(CREATE_DIRECTORY, [internalParams(peer), dirPath + '']);
            return result ? new File(result) : null;
        }

        /**
         * <p>비어 있는 새로운 파일을 생성합니다.현재 디렉토리의 상대 위치에 새로운 파일을 생성합니다. 경로에 현재 디렉토리를 나타내는 “.”과 상위 디렉토리를 나타내는 “..”의 사용은 허용되지 않습니다. 파일 생성에 성공하면 생성된 파일을 파일 핸들로 변환하여 반환합니다.<br>
         * ※ Appspresso의 현재 버전 1.0 beta에서 이미 존재하는 파일 경로를 지정한 경우 iOS는 null을 반환하고 Android 런타임은 오류 코드 IO_ERR로 DeviceAPIError 객체를 throw 합니다.<br>
         * Waikiki API 2.0 정식 버전에서 이 경우를 IO_ERR로 규정하는 내용이 추가되었습니다. Appspresso의 다음 버전에서 수정될 예정입니다.<br>
         * 새 파일을 생성하는데 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체가 던져집니다.<br>
         * </p>
         *
         * @param {DOMString} filePath <p>생성할 파일의 상대 경로입니다. 경로에는 단말의 파일시스템에서 지원하는 문자만 포함되어야 합니다.</p>
         * @memberOf File#
         * @type File
         * @returns {File} <p>새로 생성된 파일의 파일 핸들입니다. 반환된 File 객체는 createFile 함수를 호출한 File객체로부터 접근권한 “rw”를 상속받습니다.</p>
         * @exception
         * <p>
         * - INVALID_VALUES_ERR: filePath가 유효한 경로가 아닌 경우<br>
         * - TYPE_MISMATCH_ERR: 매개변수의 형식이 올바르지 않은 경우<br>
         * - IO_ERR: filePath의 파일이 이미 존재할 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @example
         * var newFile = dir.createFile("newFilePath");
         *
         */
        function createFile(filePath) {
            ax.util.validateRequiredStringParam(filePath);

            if( !peer.isDirectory ) {
                throw new DeviceAPIError(ax.IO_ERR);
            }

            filePath = convertToValidPath(filePath);

            var result = self.execSyncWAC("createFile", [internalParams(peer), filePath + '']);
            return result ? new File(result) : null;
        }

        /**
         * <p>현재 디렉토리 하위에 존재하는 파일 혹은 디렉토리를 파일 핸들로 변환하여 반환합니다. filePath에 현재 디렉토리를 나타내는 “.”과 상위 디렉토리를 나타내는 “..”의 사용은 허용되지 않습니다<br>
         * filePath는 UTF-8로 인코딩되어야 합니다.
         * </p>
         *
         * @param {DOMString} filePath <p>대상 파일 / 디렉토리의 상대 경로</p>
         * @memberOf File#
         * @type File
         * @returns {File} <p>지정된 경로에 대응하는 파일 핸들. 반환된 File 객체는 resolve 함수를 호출한 File객체로부터 접근 권한을 상속받습니다</p>
         * @exception
         * <p>
         * - NOT_FOUND_ERR : filePath의 파일이 존재하지 않는 경우<br>
         * - TYPE_MISMATCH_ERR: 매개변수의 형식이 올바르지 않은 경우<br>
         * - INVALID_VALUES_ERR: filePath에 지원되지 않는 문자가 포함되어 있는 경우<br>
         * - IO_ERR 디렉토리가 아닌 파일에서 이 함수를 호출한 경우<br>
         * - UNKNOWN_ERR: 그 밖의 다른 모든 경우<br>
         * </p>
         *
         * @example
         * var hellofile = dir.resolve("documents/hello.txt");
         *
         */
        function resolve(filePath) {
            ax.util.validateRequiredStringParam(filePath);

            filePath = convertToValidPath(filePath);

            var result = self.execSyncWAC('resolveFilePath', [internalParams(peer), filePath + '']);
            return result ? new File(result) : null;
        }

        /**
         * <p>디렉토리를 삭제합니다.<br>현재 디렉토리 하위에 존재하는 디렉토리를 비동기적으로 삭제합니다. 매개변수 recursive를 true로 지정하면 지정된 디렉토리와 그 하위의 트리까지 모두 삭제됩니다. false로 지정한 경우 디렉토리가 비어 있는 경우에만 삭제되며 그렇지 않은 경우 관련 오류를 매개변수로 하여 errorCallback 함수가 호출됩니다.<br>
         * 삭제할 디렉토리는 반드시 현재 디렉토리 하위에 있어야 하며 그렇지 않으면 연산이 수행되지 않습니다. 디렉토리 삭제에 성공한 경우 successCallback 함수가 호출되며 실패한 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.</p>
         * <p>
         * - INVALID_VALUES_ERR: 입력 매개변수가 유효한 값을 가지고 있지 않는 경우<br>
         *   예) successCallback이 null인 경우. <br>
         * - NOT_FOUND_ERR: 삭제하려는 디렉토리가 유효한 디렉토리가 아닌 경우<br>
         * - IO_ERR: 디렉토리가 아닌 파일에서 이 함수를 호출한 경우, directory가 유효한 디렉토리 경로가 아니거나 다른 프로세스에서 사용 중인 경우, 디렉토리가 비어 있지 않고 recursive 매개변수가 false인 경우, 재귀적인 삭제 중 권한 부족이나 다른 프로세스에 의해 열려진 파일 혹은 디렉토리가 있어 일부만 삭제한 상태에서 실패하고 삭제된 파일들을 복구할 수 없는 경우.<br>
         * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * - SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {FileSystemSuccessCallback} successCallback <p>디렉토리 삭제에 성공한 경우 콜백으로 호출됩니다.</p>
         * @param {ErrorCallBack} errorCallback <p>디렉토리 삭제에 실패한 경우 콜백으로 호출됩니다.(에러를 무시할 경우 null도 가능)</p>
         * @param {DOMString} directory <p>삭제할 디렉토리의 가상 절대 경로이며 반드시 현재 디렉토리 하위에 존재해야 합니다.</p>
         * @param {boolean} recursive <p>true인 경우 재귀적으로 삭제합니다.</p>
         * @memberOf File#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * function successCB(files) {
         *   for(var i = 0; files.length; i++) {
         *     if (files[i].isDirectory)
         *       documentsDir.deleteDirectory(
         *           function(){
         *             alert("File Deleted");
         *           }, function(e) {
         *             alert("Error" + e.message);
         *           }
         *           files[i].fullPath,
         *           , false
          *      );
         *     else
         *       documentsDir.deleteFile(
         *           function(){
         *             alert("Directory Deleted");
         *           }, function(e) {
         *             alert("Error" + e.message);
         *           },
         *           files[i].fullPath
         *       );
         *   }
         * }
         *
         * function errorCB(error) {
         *   alert("The error " + error.message + " occurred when listing the files in the selected folder");
         * }
         *
         * var documentsDir;
         * deviceapis.filesystem.resolve(
         *     function(dir){
         *       documentsDir = dir;
         *       dir.listFiles(successCB,errorCB);
         *     }, function(e) {
         *       alert("Error" + e.message);
         *     }, 'documents', 'rw'
         * );
         *
         */
        function deleteDirectory(successCallback, errorCallback, directory, recursive) {
            ax.util.validateRequiredStringParam(directory);
            ax.util.validateRequiredBooleanParam(recursive);
            directory = convertToValidPath(directory);
            return self.execAsyncWAC(DELETE_DIRECTORY, successCallback, errorCallback, [internalParams(peer), directory + '', recursive]);
        }

        /**
         * <p>파일을 삭제합니다.<br>
         * 현재 디렉토리의 하위에 존재하는 파일을 비동기적으로 삭제합니다. 삭제할 파일이 이 함수가 호출된 디렉토리 하위에 존재해야 하며 그렇지 않은 경우 연산이 수행되지 않습니다. 파일 삭제에 성공하면 successCallback 함수가 호출됩니다.<br>
         * 파일 삭제에 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.</p>
         * <p>
         * - INVALID_VALUES_ERR: 입력 매개변수가 유효한 값을 가지고 있지 않는 경우<br>
         *   예) successCallback이 null인 경우. <br>
         * - NOT_FOUND_ERR: 삭제하려는 파일이 유효한 파일이 아닌 경우<br>
         * - IO_ERR: 디렉토리가 아닌 파일에서 이 함수를 호출한 경우, file이 다른 프로세스에서 사용 중인 경우, 파일 시스템에 대한 권한이 없는 경우<br>
         * - NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * - SECURITY_ERR: 연산이 허용되지 않는 경우<br>
         * - UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {FileSystemSuccessCallback} successCallback <p>파일 삭제에 성공한 경우 콜백으로 호출됩니다.</p>
         * @param {ErrorCallBack} errorCallback <p>파일 삭제에 실패한 경우 콜백으로 호출됩니다.</p>
         * @param {DOMString} file <p>삭제할 파일의 가상 절대 경로이며 반드시 현재 디렉토리 하위에 존재해야 합니다.</p>
         * @memberOf File#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * function successCB(files) {
         *   for(var i = 0; files.length; i++) {
         *     if (files[i].isDirectory)
         *       documentsDir.deleteDirectory(
         *           files[i].fullPath,
         *           function(){
         *             alert("File Deleted");
         *           }, function(e) {
         *             alert("Error" + e.message);
         *           }, false
         *       );
         *     else
         *       documentsDir.deleteFile(
         *           function(){
         *             alert("Directory Deleted");
         *           }, function(e) {
         *             alert("Error" + e.message);
         *           },
         *           files[i].fullPath
         *       );
         *   }
         * }
         *
         * function errorCB(error) {
         *   alert("The error " + error.message + " occurred when listing the files in the selected folder");
         * }
         *
         * var documentsDir;
         * deviceapis.filesystem.resolve(
         *   function(dir){
         *     documentsDir = dir;
         *     dir.listFiles(successCB,errorCB);
         *   }, function(e){
         *     alert("Error" + e.message);
         *   }, 'documents', 'rw'
         * );
         *
         */
        function deleteFile(successCallback, errorCallback, file) {
            ax.util.validateRequiredStringParam(file);
            file = convertToValidPath(file);
            return self.execAsyncWAC(DELETE_FILE, successCallback, errorCallback, [internalParams(peer), file + '']);
        }

        ax.def(this)
            .property(PARENT, getParent)
            .property(READONLY, getReadOnly)
            .property(CREATED, getCreated)
            .property(MODIFIED, getModified)
            .property(FILESIZE, getFileSize)
            .property(LENGTH, getLength)
            .constant('isFile', peer.isFile)
            .constant('isDirectory', peer.isDirectory)
            .constant('path', peer.path)
            .constant('name', peer.name)
            .constant('fullPath', peer.fullPath)
            .method(TO_URI, toURI)
            .method(LIST_FILES, listFiles)
            .method(OPEN_STREAM, openStream)
            .method(READ_AS_TEXT, readAsText)
            .method(COPY_TO, copyTo)
            .method(MOVE_TO, moveTo)
            .method(CREATE_DIRECTORY, createDirectory)
            .method(CREATE_FILE, createFile)
            .method(RESOLVE_FILE_PATH, resolve)
            .method(DELETE_DIRECTORY, deleteDirectory)
            .method(DELETE_FILE, deleteFile);
    }

    ////////////////////////////////////////////////////////////////////////////
    // constructor FileStream

    var EOF = 'eof';
    var IS_EOF = 'isEOF';
    var POSITION = 'position';
    var BYTES_AVAILABLE = 'bytesAvailable';
    var GET_BYTES_AVAILABLE = 'getBytesAvailable';
    var CLOSE = 'close';
    var FILIE_STREAM_READ = 'read';
    var READ_BYTES = 'readBytes';
    var READ_BASE64 = 'readBase64';
    var WRITE = 'write';
    var WRITE_BYTES = 'writeBytes';
    var WRITE_BASE64 = 'writeBase64';

    /**
     * <p>FileStream은 읽기/쓰기 연산을 위해 열린 파일에 대한 핸들을 표현합니다. 읽기와 쓰기 연산은 파일에서의 현재 위치를 가리키는 파일 포인터와 결부되어 수행됩니다. 이 인터페이스의 일련의 읽기/쓰기 함수들은 이진 파일과 텍스트 파일 모두에 대해 사용 가능합니다.<br>
     * 파일 스트림을 닫은 후에 이 스트림에 어떤 연산을 시도하는 경우에는 일반적인 JavaScript 오류가 발생합니다.<br>
     * 이 인터페이스의 읽기/쓰기 연산은 최초의 resolve 함수 혹은 File 인터페이스의 openStream 함수에서 이미 필요한 접근 권한을 획득했기 때문에 어떠한 보안관련 오류도 발생하지 않습니다.<br>
     * </p>
     *
     * @class FileStream은 읽기/쓰기 연산을 위해 열린 파일에 대한 핸들을 표현합니다
     * @name FileStream
     */
    function FileStream() {
        var peer = arguments[0];

        var self = this;

        /**
         * <p>파일 포인터가 파일의 끝에 도달했는지 여부를 가리킵니다.<br>파일 포인터가 현재 파일 스트림의 끝에 도달한 경우 true입니다.<br>이 속성은 읽기 전용입니다.</p>
         *
         * @field
         * @type boolean
         * @memberOf FileStream.prototype
         * @name eof
         *
         * @example
         * if(stream.eof) {
         *   // file has been read completely
         * }
         *
         */
        function isEof() {
            return self.execSyncWAC(IS_EOF, [peer._handle]);
        }

        /**
         * <p>읽기/쓰기를 위해 파일 포인터의 위치를 설정합니다.<br>position 속성은 파일 스트림의 시작점을 기준으로 한 바이트 단위의 위치입니다. 파일 스트림에 대한 읽기/쓰기 연산은 이 position 속성의 위치에서 수행됩니다.<br>
         * ※ Appspresso 현재 버전 1.0 beta의 Android 런타임은 가상 루트 wgt-package의 하위에 있는 파일들에 대해서는 이 속성을 지원하지 못합니다. 가상 루트 wgt-package의 하위에 있는 파일들에 대해서는 read 계열의 함수를 이용해 파일 스트림의 시작점부터 순차적으로 읽어 나가야 합니다.<br>
         * ※ "a"로 파일을 열 경우 이 속성에 대한 설정은 무시됩니다.<br>
         * - DeviceAPIError: <br>
         * - IO_ERR: 설정하려는 위치가 파일 스트림의 영역을 벗어나는 경우<br>
         * </p>
         *
         * @field
         * @type long
         * @memberOf FileStream.prototype
         * @name position
         *
         * @example
         * alert(stream.position); // displays current stream position
         * // alters current stream position to the begin of the file,
         * // like seek() in C
         * stream.position = 0;
         *
         */
        function setPosition(position) {
            ax.util.validateRequiredNumberParam(position);

            if (position < 0) {
                throw new DeviceAPIError(ax.IO_ERR, 'position must be greater than zero');
            }

            return self.execSyncWAC('setPosition', [peer._handle, position - 0]);
        }

        function getPosition() {
            return self.execSyncWAC('getPosition', [peer._handle]);
        }

        /**
         * <p>스트림에서 읽을 수 있는 바이트 수를 반환합니다.<br>다음 읽기 연산에서 읽을 수 있는 최대 바이트 수입니다. eof 속성이 true인 경우 -1입니다.<br>이 속성은 읽기 전용입니다.<br>
         * ※ Appspresso 현재 버전 1.0 beta의 Android 런타임은 가상 루트 wgt-package의 하위에 있는 파일들에 대해서는 이 값이 실제보다 작을 수 있습니다. 가상 루트 wgt-package의 하위에 있는 파일들에 대해서는 eof 속성이 true가 될 때까지 순차적으로 읽어야 합니다.</p>
         *
         * @field
         * @type long
         * @memberOf FileStream.prototype
         * @name bytesAvailable
         *
         * @example
         * alert(stream.bytesAvailable); // displays the available bytes to be read</p>
         */
        function getBytesAvailable() {
            return self.execSyncWAC(GET_BYTES_AVAILABLE, [peer._handle]);
        }

        /**
         * <p>파일 스트림을 닫습니다.
         * <p>쓰기가 지연되어 버퍼에 남아 있는 데이터가 있다면 이를 디스크에 쓰고 파일을 닫습니다. 이 연산은 항상 성공합니다. 단, 쓰기가 지연되어 있던 데이터를 디스크에 쓰는 작업은 실패할 가능성이 있습니다.</p>
         *
         * @memberOf FileStream#
         * @type void
         * @returns {void}
         *
         * @exception
         * stream.close(); // closes this stream, no subsequent access to stream allowed
         *
         */
        function close() {
            return self.execSyncWAC(CLOSE, [peer._handle]);
        }

        /**
         * <p>지정한 문자 수만큼 파일 스트림에서 읽습니다.<br>지정된 수 만큼의 문자들을 읽어 문자열로 반환합니다. 읽는 도중 eof 속성이 true가 되면 반환된 문자열이 지정한 길이보다 짧을 수 있습니다.</p>
         *
         * @param {long} charCount <p>읽을 문자의 수</p>
         * @memberOf FileStream#
         * @type DOMString
         * @returns {DOMString} <p>읽은 문자열</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.<br>IO_ERR: 읽는 중 오류가 발생한 경우</p>
         *
         * @example
         * var text = stream.read(file.fileSize);
         * stream.close();
         *
         */
        function read(charCount) {
            ax.util.validateRequiredNumberParam(charCount);
            return self.execSyncWAC(FILIE_STREAM_READ, [peer._handle, charCount]);
        }

        /**
         * <p>지정한 바이트 수만큼 파일 스트림에서 읽습니다.</p>
         *
         * @param {long} byteCount <p>읽을 바이트 수</p>
         * @memberOf FileStream#
         * @type ByteArray
         * @returns {ByteArray} <p>읽은 바이트 배열</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.<br>IO_ERR: 읽는 중 오류가 발생한 경우</p>
         *
         * @example
         * // reads up to 256 bytes from the stream
         * var raw = stream.readBytes(256);
         * for(var i = 0; i < raw.length; i++) {
         *   // raw[i] contains the i-th byte of the current data chunk
         * }
         *
         */
        function readBytes(byteCount) {
            ax.util.validateRequiredNumberParam(byteCount);
            return self.execSyncWAC(READ_BYTES, [peer._handle, byteCount]);
        }

        /**
         * <p>지정한 바이트 수만큼 파일 스트림에서 읽고 base64로 인코딩하여 반환합니다.</p>
         *
         * @param {long} byteCount <p>읽을 바이트 수</p>
         * @memberOf FileStream#
         * @type DOMString
         * @returns {DOMString} <p>base64로 인코딩 된 문자열</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.<br>IO_ERR: 읽는 중 오류가 발생한 경우</p>
         *
         * @example
         * // reads up to 256 bytes from the stream
         * var base64 = stream.readBase64(256);
         *
         */
        function readBase64(byteCount) {
            ax.util.validateRequiredNumberParam(byteCount);
            return self.execSyncWAC(READ_BASE64, [peer._handle, byteCount]);
        }

        /**
         * <p>지정한 문자열을 파일 스트림에 씁니다.</p>
         *
         * @param {DOMString} stringData <p>파일에 쓸 문자열.</p>
         * @memberOf FileStream#
         * @type void
         * @returns {void}
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.<br>IO_ERR: 읽는 중 오류가 발생한 경우</p>
         *
         * @example
         * var text = "Hello world";
         * stream.write(text);
         *
         */
        function write(stringData) {
            self.execSyncWAC(WRITE, [peer._handle, stringData]);
        }

        /**
         * <p>지정한 바이트 배열을 파일 스트림에 씁니다.</p>
         *
         * @param {ByteArray} byteData <p>파일에 쓸 바이트 배열</p>
         * @memberOf FileStream#
         * @type void
         * @returns {void}
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.<br>IO_ERR: 읽는 중 오류가 발생한 경우</p>
         *
         * @example
         * var bytes = in.readBytes(256);
         * out.writeBytes(bytes); // writes the bytes read from in to out
         *
         */
        function writeBytes(byteData) {
            this.execSyncWAC(WRITE_BYTES, [peer._handle, byteData]);
        }

        /**
         * <p>base64로 인코딩된 문자열을 바이트로 변환한 후 파일 스트림에 씁니다.</p>
         *
         * @param {DOMString} base64Data <p>파일에 쓸 base64로 인코딩 된 문자열</p>
         * @memberOf FileStream#
         * @type void
         * @returns {void}
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.<br>IO_ERR: 읽는 중 오류가 발생한 경우</p>
         *
         * @example
         * var base64 = in.readBase64(256);
         * out.writeBase64(base64); // writes the base64 data read from in to out
         *
         */
        function writeBase64(base64Data) {
            this.execSyncWAC(WRITE_BASE64, [peer._handle, base64Data]);
        }

        ax.def(this)
            .property(EOF, isEof)
            .property(POSITION, getPosition, setPosition)
            .property(BYTES_AVAILABLE, getBytesAvailable)
            .method(CLOSE, close)
            .method(FILIE_STREAM_READ, read)
            .method(READ_BYTES, readBytes)
            .method(READ_BASE64, readBase64)
            .method(WRITE, write)
            .method(WRITE_BYTES, writeBytes)
            .method(WRITE_BASE64, writeBase64);
    }

    ////////////////////////////////////////////////////////////////////////////
    // constructor FileFilter
    /**
     * <p>listFiles 함수에서 반환하는 항목들을 제한하기 위한 필터입니다.<br>listFiles 함수에 이 필터를 사용하면 이 필터에 부합하는 파일들만 반환됩니다. 필터의 속성들 중 undefined나 null아닌 모든 속성과 파일의 속성이 일치하는 경우에 필터에 부합됩니다. 검색은 SQL의 “AND” 연산과 유사한 방식으로 수행됩니다<br>다음 규칙에 따라 파일의 속성과 필터의 속성이 일치되는지 여부를 결정합니다.<br>
     * 필터의 문자열 속성의 경우 U+0025 'PERCENT SIGN' 와일드카드 문자(열)를 포함하지 않는다면 필터와 파일의 속성 값이 정확히 일치해야 합니다. 와일드카드 문자가 사용된다면 SQL의 “LIKE” 조건과 유사한 방식으로 처리됩니다. (%는 빈 문자열을 포함하여 모든 문자열에 대응됩니다.) <br>
     * Date 형식의 속성의 경우 시작시간과 종료시간을 지정하는 한쌍의 속성이 적용됩니다. 시작시간 속성과 종료시간 속성 모두에 null이 아닌 값을 지정한 경우에는 파일의 시간 속성이 그 사이에 있는 경우에 필터와 부합합니다. 시작시간만을 null이 아닌 값으로 지정한 경우에는 파일의 시간 속성이 그보다 나중인 경우에 필터와 부합하며 종료 시간만을 null이 아닌 값으로 지정한 경우에는 파일의 시간 속성이 그보다 먼저인 경우에 필터와 부합합니다.<br>
     * </p>
     *
     * @class
     * @name FileFilter
     * @property {DOMString} name 파일의 이름을 필터링합니다. 이름이 이 속성과 부합(정확히 일치하거나 와일드카드와 대응)하는 파일들이 이 필터링 조건을 만족합니다.
     * @property {DOMString} startModified 파일의 수정시간 속성을 필터링합니다.<br>파일의 수정시간이 이 속성보다 나중 시간인 경우 이 필터링 조건을 만족합니다.
     * @property {DOMString} endModified 파일의 수정시간 속성을 필터링합니다.<br>파일의 수정시간이 이 속성보다 빠른 시간인 경우 이 필터링 조건을 만족합니다.
     * @property {DOMString} startCreated 파일의 생성시간 속성을 필터링합니다.<br>파일의 생성시간이 이 속성보다 나중 시간인 경우 이 필터링 조건을 만족합니다.
     * @property {DOMString} endCreated <p>파일의 생성시간 속성을 필터링합니다.<br>파일의 생성시간이 이 속성보다 빠른 시간인 경우 이 필터링 조건을 만족합니다.<br>
     * ※ 현재 Android의 파일시스템은 파일 생성시간을 지원하지 않습니다. 따라서 Appspresso의 Android 런타임에서는 파일 생성시간에 대한 필터링 조건을 적용하지 않습니다.</p>
     *
     */
    function FileFilter(name, startModified, endModified, startCreated, endCreated) {
        try {
            if (name && typeof name !== 'string') {
                throw new Error('invalid name');
            }

            if (startModified && !(startModified instanceof Date)) {
                throw new Error('invalid startModified');
            }

            if (endModified && !(endModified instanceof Date)) {
                throw new Error('invalid endModified');
            }

            if (startCreated && !(startCreated instanceof Date)) {
                throw new Error('invalid startCreated');
            }

            if (endCreated && !(endCreated instanceof Date)) {
                throw new Error('invalid endCreated');
            }
        }
        catch (e) {
            throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR, e);
        }

        this.name = name;                    // attribute DOMString name;
        this.startModified = startModified;    // attribute Date startModified;
        this.endModified = endModified;        // attribute Date endModified;
        this.startCreated = startCreated;    // attribute Date startCreated;
        this.endCreated = endCreated;        // attribute Date endCreated;
    }

    // ====================================================
File.prototype = ax.plugin("deviceapis.filesystem", {});
FileStream.prototype = ax.plugin("deviceapis.filesystem", {});
FileSystemManager.prototype = ax.plugin(FILE_SYSTEM_MANAGER, {
    'resolve': resolve
});
ax.def(FileSystemManager.prototype).property(MAX_PATH_LENGTH, function() {
    return this.execSyncWAC('getMaxPathLength');
});
ax.def(g)
    .constant('FileFilter', FileFilter)
    .constant('FileSystemManager', FileSystemManager);
ax.def(deviceapis).constant('filesystem', new FileSystemManager());
}(window));



/**
 * <p>파일을 열기 위한 성공 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name FileOpenSuccessCallback
 */

/**
 * <p>비동기 호출이 성공한 경우 호출됩니다.</p>
 * @function
 * @param {FileStream} filestream <p>파일 스트림</p>
 * @return {void}
 * @name onsuccess
 * @memberOf FileOpenSuccessCallback
 */

/**
 * <p>파일 목록을 얻기 위한 파일 시스템 모듈 전용의 성공 콜백함수를 정의합니다.</p>
 * @namespace
 * @name FileSystemListSuccessCallback
 */

/**
 * <p>비동기 호출이 성공한 경우 호출됩니다.</p>
 * @function
 * @param {FileArray} files <p>File 객체 배열</p>
 * @return {void}
 * @name onsuccess
 * @memberOf FileSystemListSuccessCallback
 */

/**
 * <p>파일 시스템 모듈 전용의 성공 콜백함수를 정의합니다.</p>
 * @namespace
 * @name FileSystemSuccessCallback
 */

/**
 * <p>비동기 호출이 성공한 경우 호출됩니다.</p>
 * @function
 * @param {File} file <p>반환된 파일</p>
 * @return {void}
 * @name onsuccess
 * @memberOf FileSystemSuccessCallback
 */

/**
 * <p>파일의 내용을 문자열로 읽기 위한 콜백함수를 정의합니다.</p>
 * @namespace
 * @name ReadFileAsStringSuccessCallback
 */

/**
 * <p>비동기 호출이 성공한 경우 호출됩니다.</p>
 * @function
 * @param {DOMString} fileStr <p>파일을 읽은 결과 문자열</p>
 * @return {void}
 * @name onsuccess
 * @memberOf ReadFileAsStringSuccessCallback
 */
