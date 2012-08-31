/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Media Extension API
 * @version 1.0
 */
(function(g) {
    "use strict";
    var _DEBUG = !!g._APPSPRESSO_DEBUG;
    //-------------------------------------------------------------

    var PLUGIN_ID = 'ax.ext.media';
    var PLUGIN_NS = 'ax.ext.media';

    /**
     * pickImage 옵션 파라미터로 사용될 객체
     *
     * @class
     * @name PickImageOpts
     * @property {string} out (optional; default:auto) - 저장될 경로.
     * @property {boolean} crop (optional; default:false) - crop 여부. 안드로이드의 경우 단말에 따라 지원되지 않을 수 있습니다.
     * @memberOf ax.ext.media
     * @see ax.ext.media.pickImage
     */

    /**
     * pick an image(choose a picture from gallery).
     * 겔러리에서 이미지를 선택합니다.
     *
     * @param {function} callback
     * @param {function} errback
     * @param {ax.ext.media.PickImageOpts} opts 저장 경로/crop 여부 지정
     * @return AxRequest
     * @methodOf ax.ext.media
     *
     * @example
     * var scb = function(file){
     *     console.log(file);
     *
     *     deviceapis.filesystem.resolve(
     *         function(file){
     *             document.body.innerHTML = '<img src="' + file.toURI() + '"/>';
     *         }, function(e){
     *             console.log('resolve err:' + e.message);
     *         }, file,  "r"
     *     );
     * };
     * var ecb = function(e){
     *     console.log(e.message);
     * };
     * // for android - 안드로이드는 removable만 지원합니다.
     * // var opts = {'crop':true, 'out': 'removable/pickedImage.jpg'};
     * var opts = {'crop':true, 'out': 'images/pickedImage.jpg'};
     * ax.ext.media.pickImage(scb, ecb, opts);
     */
    function pickImage(callback, errback, opts) {
        var wrapper = function(result) {
            if (result) {
                return callback(result);
            }

            return errback(ax.error(ax.UNKNOWN_ERR, 'missing file path'));
        };
        return this.execAsync('pickImage', wrapper, errback, [ opts || {} ]);
    }

    /**
     *
     * captureImage 옵션 파라미터로 사용될 객체
     *
     * @class
     * @name CaptureImageOpts
     * @property {string} out (optional; default:auto) - 저장될 경로.
     * @property {boolean} crop (optional; default:false) - crop 여부. 안드로이드의 경우 단말에 따라 지원되지 않을 수 있습니다.
     * @memberOf ax.ext.media
     * @see ax.ext.media.captureImage
     */

    /**
     * capture an image(take a picture with camera).
     *
     * @param {function} callback
     * @param {function} errback
     * @param {ax.ext.media.CaptureImageOpts} opts
     * @return AxRequest
     * @methodOf ax.ext.media
     *
     * @example
     * var scb = function(file){
     *     console.log(file);
     *
     *     deviceapis.filesystem.resolve(
     *         function(file){
     *             document.body.innerHTML = '<img src="' + file.toURI() + '"/>';
     *         }, function(e){
     *             console.log('resolve err:' + e.message);
     *         }, file,  "r"
     *     );
     * };
     * var ecb = function(e){
     *     console.log(e.message);
     * };
     *
     * // for android - 안드로이드는 removable만 지원합니다.
     * // var opts = {'crop':true, 'out': 'removable/capturedImage.jpg'};
     * var opts = {'crop':true, 'out': 'images/capturedImage.jpg'};
     * ax.ext.media.captureImage(scb, ecb, opts);
     */
    function captureImage(callback, errback, opts) {
        var wrapper = function(result) {
            if (result) {
                return callback(result);
            }

            return errback(ax.error(ax.UNKNOWN_ERR, 'missing file path'));
        };
        return this.execAsync('captureImage', wrapper, errback, [ opts || {} ]);
    }

    /**
     *
     * captureImage 옵션 파라미터로 사용될 객체
     *
     * @class
     * @name CaptureScreenOpts
     * @property {string} out (optional; default:auto) - 저장될 경로.
     * @memberOf ax.ext.media
     * @see ax.ext.media.captureScreen
     */

    /**
     * capture the current screen(take a screenshot).
     *
     * @param {function} callback
     * @param {function} errback
     * @param {ax.ext.media.CaptureScreenOpts} opts
     * @return AxRequest
     * @methodOf ax.ext.media
     *
     * @example
     * var scb = function(file){
     *     console.log(file);
     *
     *     deviceapis.filesystem.resolve(
     *         function(file){
     *             document.body.innerHTML = '<img src="' + file.toURI() + '"/>';
     *         }, function(e){
     *             console.log('resolve err:' + e.message);
     *         }, file,  "r"
     *     );
     * };
     * var ecb = function(e){
     *     console.log(e.message);
     * };
     *
     * // for android - 안드로이드는 removable만 지원합니다.
     * // var opts = {'out': 'removable/capturedScreenImage.jpg'};
     * var opts = {'out': 'images/capturedScreenImage.jpg'};
     * ax.ext.media.captureScreen(scb, ecb, opts);
     */
    function captureScreen(callback, errback, opts) {
        return this.execAsync('captureScreen', callback, errback, [ opts || {} ]);
    }

    /**
     * @class
     * @name TransformImageOpts
     * @property {number} newSize
     * @memberOf ax.ext.media
     */

    /**
     * transform the image.
     *
     * @param {ax.ext.media.TransformImageOpts} opts
     * @methodOf ax.ext.media
     * @private
     */
    function transformImage(opts) {
        this.execSync('transformImage', [ opts || {} ]);
    }

    /**
     * add the image to gallery(photo album).
     *
     * @param {string} path
     * @methodOf ax.ext.media
     * @private
     */
    function addToGallery(path) {
        this.execSync('addToGallery', [ path ]);
    }

    /**
     * play the audio file.
     *
     * @param {string} path
     * @methodOf ax.ext.media
     * @private
     */
    function playAudio(path) {
        this.execAsync('playAudio', ax.nop, ax.nop, [ path ]);
    }

    /**
     *  play the audio file.
     *
     * @param {string} path
     * @methodOf ax.ext.media
     */
    function playSound(path) {
        var self = this;

        self.execAsync('playSound', function(streamId) {
            setTimeout(function() {
                self.execAsync('stopSound', ax.nop, ax.nop, [ streamId ]);
            }, 10000);
        }, ax.nop, [ path ]);
    }

    //-------------------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'pickImage': pickImage,
        'captureImage': captureImage,
        'captureScreen': captureScreen,
        'playSound':playSound,
        //XXX: undocumented!
        'transformImage': transformImage,
        'addToGallery': addToGallery,
        'playAudio': playAudio
    }, PLUGIN_NS);
}(window));
