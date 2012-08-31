/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Contact Extension API
 * @version 1.0
 */

(function() {
    "use strict";

    var NS_CONTACT = "ax.ext.contact";
    var PREFIX_CONTACT = "ax.ext.contact";

    /**
     * Contact Extension API - 핸드폰의 주소록을 띄웁니다.
     * 선택 시 해당 유저의 이름과 전화번호 정보를 리턴.
     *
     * @namespace
     * @name ax.ext.contact
     */

    /**
     * pick a contact
     *
     * @param {function} callback
     * @param {function} errback
     * @return AxRequest
     * @memberOf ax.ext.contact
     *
     * @example
     * var scb = function(addr) {
     *     try{
     *         console.log(addr.firstName);
     *         console.log(addr.lastName);
     *         console.log(addr.phoneNumbers[0]);
     *     }catch(e){
     *         console.log(e.message);
     *     }
     * };
     * var ecb = function(e) {
     *     console.log(e.message);
     * };
     *
     * ax.ext.contact.pickContact(scb, ecb);
     */
    function pickContact(callback, errback) {
        this.execAsync('pickContact', callback, errback);
    }

    ax.plugin(PREFIX_CONTACT, {
        'pickContact' : pickContact,
    }, NS_CONTACT);
})();
