/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Zip Extension API
 * @version 1.0
 */
(function(g) {
    "use strict";
    var _DEBUG = !!g._APPSPRESSO_DEBUG;
    //-------------------------------------------------------------

    var PLUGIN_ID = 'ax.ext.zip';
    var PLUGIN_NS = 'ax.ext.zip';

    /**
     * Zip Extension API
     * @namespace
     * @name ax.ext.zip
     */

    /**
     * unzip the zip file.
     *
     * @param {function} callback
     * @param {function} errback
     * @param {string} path
     * @param {string} targetDir
     * @return AxRequest
     * @methodOf ax.ext.zip
     *
     * @example
     * var scb = function() {
     *     console.log('file unzipped');
     * };
     * var ecb = function(e) {
     *     console.log(e.message);
     * };
     * ax.ext.zip.unzip(scb, ecb, 'downloads/abcd.zip', 'documents');
     */
    function unzip(callback, errback, path, targetDir) {
        return this.execAsync('unzip', callback, errback, [ path, targetDir ]);
    }

    //-------------------------------------------------------------
    ax.plugin(PLUGIN_ID, {
        'unzip': unzip
    }, PLUGIN_NS);
}(window));
