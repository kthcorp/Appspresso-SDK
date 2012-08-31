/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview Contact Module. <p>주소록(address book)은 연락처(contact)의 집합입니다. 연락처는 전화번호, 전자메일 주소 등과 같이 타인과 연락하기 위해 필요한 정보들의 집합입니다. RFC 2426 vCard MIME Directory Profile 규격은 연락처를 교환하기 위한 형식을 정의합니다.<br>
 * 이 API는 특정 주소록에서 연락처를 읽고 쓰고 삭제하고 갱신할 수 있는 기능을 제공합니다. 주소록은 AddressBook 객체들을 반환하는 getAddressBooks 함수를 통해 얻을 수 있습니다.</p>
 * <p>
 * http://wacapps.net/api/pim.contact 피쳐 혹은 이 피쳐의 하위 피쳐들이 요청되면 ContactManager 인터페이스의 인스턴스가 deviceapis.pim.contact 으로 전역 네임 스페이스에 생성됩니다.<br>
 * ※  Appspresso는 파일을 직접 편집하지 않고도 피쳐를 선언할 수 있는 그래픽 편집기를 제공합니다. 그래픽 편집기를 이용한 피쳐 선언 방법은 <a href="http://appspresso.com/ko/archives/2564">project.xml 설정</a>을 참고하십시오.<br><br>
 * 다음은 위젯의 config.xml 파일을 통해 이 API의 피쳐들을 선언하기 위한 URI의 목록입니다.<br>
 * <strong>http://wacapps.net/api/pim.contact</strong><br>
 * 모든 기능을 사용할 수 있습니다.<br>
 * <strong>http://wacapps.net/api/pim.contact.read</strong><br>
 * 주소록에서 읽기 위한 getAddressBooks, findContacts 함수에 접근합니다.<br>
 * <strong>http://wacapps.net/api/pim.contact.write</strong><br>
 * 주소록의 연락처를 생성, 추가, 삭제, 갱신하는 addContact, deleteContact, createContact, updateContact 함수에 접근합니다.<br>
 * </p>
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');
    var deviceapis = g.deviceapis;// require('deviceapis');
    var pim = g.deviceapis.pim;// require('deviceapis.pim');

    ////////////////////////////////////////////////////////////////////////////
    // convenient functions, variables

    var SIM_ADDRESS_BOOK = 0;
    var DEVICE_ADDRESS_BOOK = 1;

    function initNewContact(id, contact) {
        var newContact = new Contact(id);

        if (contact) {
            newContact.firstName = contact.firstName;
            newContact.middleName = contact.middleName;
            newContact.lastName = contact.lastName;
            newContact.nicknames = contact.nicknames;
            newContact.phoneticName = contact.phoneticName;
            newContact.addresses = contact.addresses;
            newContact.photoURI = contact.photoURI;
            newContact.phoneNumbers = contact.phoneNumbers;
            newContact.emails = contact.emails;
        }

        return newContact;
    }

    function castAsString(value) {
        return (value === null || value === undefined) ? value : value + '';
    }

    ////////////////////////////////////////////////////////////////////////////
    // typedef

    ax.def(g)
        .constant('AddressBookArray', Array)
        .constant('ContactArray', Array)
        .constant('PhoneNumberArray', Array)
        .constant('EmailAddressArray', Array)
        .constant('ContactAddressArray', Array);

    ////////////////////////////////////////////////////////////////////////////
    // interface ContactManager

    var CONTACT_MANAGER = 'ContactManager';
    var CONTACT = 'contact';

    /**
     * <p>contact 모듈의 관리자 인터페이스입니다. 이 인터페이스는 주소록을 얻을 수 있는 함수를 제공합니다. 주소록에 대한 참조를 얻은 후에는 연락처를 추가, 삭제, 갱신할 수 있습니다.</p>
     * @class contact 모듈의 관리자 인터페이스입니다.
     * @name ContactManager
     */
    function ContactManager() {
    }

    var GET_ADDRESS_BOOKS = 'getAddressBooks';

    /**
     * <p>이용 가능한 주소록을 얻습니다.<br>이 함수가 성공하면 사용 가능한 주소록 객체들의 배열을 매개변수로 successCallback 함수가 호출됩니다. 사용 가능한 주소록이 존재하지 않으면 빈 배열이 전달됩니다.<br>
     * 주소록을 얻는 데 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
     * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
     * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
     * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
     * </p>
     * @param {AddressBookArraySuccessCallback} successCallback <p>주소록을 얻는 데 성공한 경우 콜백으로 호출됩니다</p>
     * @param {ErrorCallback} errorCallback <p>주소록을 얻는 데 실패한 경우 콜백으로 호출됩니다.</p>
     * @memberOf ContactManager#
     * @type PendingOperation
     * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
     * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
     *
     * @example
     * var addressbook;
     *
     * // Define the error callback for all the asynchronous calls
     * function errorCallback(response) {
     *   alert( "The following error occurred: " +  response.code);
     * }
     *
     * function contactUpdatedCB() {
     *   // The contact has been successfully updated
     *   alert('Contact Successfully updated');
     * }
     *
     * function contactFoundCB(contacts) {
     *   // The contact has been successfully found
     *   // Let's try to change the summary
     *   contacts[0].firstName = "Jeffrey Ross";
     *   addressbook.updateContact(contactUpdatedCB,
     *                                errorCallback,
     *                                  contacts[0]);
     * }
     *
     * function contactAddedCB(contact) {
     *   // The contact has been successfully added
     *   // Let's try to check if we can retrieve the added
     *   // contact from the address book. If the address book
     *   // was empty only the item added through addContact should
     *   // be returned
     *   addressbook.findContacts(contactFoundCB, errorCallback,
     *                                {firstName:'%Jeffrey%'});
     *  }
     *
     * // Define the success callback for retrieveing all the
     * // Address Books
     * function AddressBooksCB(addressbooks) {
     *   if(addressbooks.length > 0)
     *   {
     *     addressbook = addressbooks[0];
     *     alert("The addressbook type is " + addressbook.type +
     *           " and name " + addressbook.name);
     *
     *     var contact = addressbook.createContact(
     *                     {firstName:'Jeffrey',
     *                      lastName:'Hyman',
     *                      nicknames:['joey ramone'],
     *                      emails:[{email:'user@domain.com'}],
     *                      phoneNumbers:[{number:'666666666'}]});
     *
     *     addressbook.addContact(contactAddedCB, errorCallback,
     *                                                 contact);
     *   }
     *  }
     *
     *  // Get a list of available Address Books.
     *  deviceapis.pim.contact.getAddressBooks(AddressBooksCB,
     *                                          errorCallback);
     */
    function getAddressBooks(successCallback, errorCallback) {
        function scb(result) {
            var i, books = [];

            for (i = 0; i < result.length; i++) {
                books[i] = new AddressBook(result[i]);
            }

            ax.util.invokeLater(null, successCallback, books);
        }

        ax.util.validateCallback(successCallback, errorCallback);
        return this.execAsyncWAC(GET_ADDRESS_BOOKS, scb, errorCallback, []);
    }

    ////////////////////////////////////////////////////////////////////////////
    // interface AddressBook

    var CREATE_CONTACT = 'createContact';
    var ADD_CONTACT = 'addContact';
    var UPDATE_CONTACT = 'updateContact';
    var DELETE_CONTACT = 'deleteContact';
    var FIND_CONTACTS = 'findContacts';

    /**
     * <p>주소록의 추상화 표현.주소록은 연락처의 집합입니다. 이 인터페이스는 주소록에서 연락처를 관리할 수 있도록 다음과 같은 함수들을 제공합니다.<br>
     * •findContact 함수와 필터로 연락처를 검색합니다.<br>
     * •addContact함수로 특정 주소록에 연락처를 추가합니다.<br>
     * •updateContact 함수로 기존 연락처 정보를 갱신합니다.<br>
     * •deleteContact 함수로 연락처를 삭제합니다.<br>
     * </p>
     * @class 주소록의 추상화 표현
     * @name AddressBook
     */
    function AddressBook(addrBook) {
        var self = this;

        /**
         * <p>ContactProperties 인터페이스를 기반으로 Contact 객체를 생성합니다.<br>이 함수는 Contact 인터페이스의 객체를 생성하고 반환합니다.<br>
         * 매개변수 contactProperties 객체를 생략한 경우 반환되는 Contact 객체의 모든 속성은 기본 값을 갖습니다. contactProperties의 속성들 중 일부에만 값이 지정된 경우 반환되는 Contact 객체의 속성들 중 나머지 속성들은 기본 값을 갖습니다.<br>
         * 이 연산은 생성된 연락처를 주소록에 추가하거나 id 속성을 부여하지 않습니다. 이렇게 하려면 개발자가 직접 addContact 함수를 호출해야 합니다.<br>
         * </p>
         *
         * @param {ContactProperties} contactProperties <p>연락처 정보</p>
         * @memberOf AddressBook#
         * @type Contact
         * @returns {Contact} <p>생성된 Contact 객체</p>
         * @exception <p>INVALID_VALUES_ERR: ContactProperties에 유효하지 않은 값이 포함된 경우<br>
         * NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * TYPE_MISMATCH_ERR: 매개변수의 형식이 올바르지 않은 경우<br>
         * UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @example
         * var addressbook;
         *
         * // Define the error callback for all the asynchronous calls
         * function errorCallback(response) {
         *   alert( "The following error occurred: " +  response.code);
         * }
         *
         * function contactAddedCB(contact) {
         *    alert("Contact Added with id " + contact.id);
         * }
         *
         * // Define the success callback for retrieveing all the
         * // Address Books
         * function AddressBooksCB(addressbooks) {
         *   if(addressbooks.length > 0)
         *   {
         *     addressbook = addressbooks[0];
         *     alert("The addressbook type is " + addressbook.type +
         *           " and name " + addressbook.name);
         *
         *     var contact = addressbook.createContact(
         *                     {firstName:'Jeffrey',
         *                      lastName:'Hyman',
         *                      nicknames:['joey ramone'],
         *                      emails:[{email:'user@domain.com'}],
         *                      phoneNumbers:[{number:'666666666'}]});
         *
         *     addressbook.addContact(contactAddedCB, errorCallback, contact);
         *   }
         * }
         *
         *  // Get a list of available Address Books.
         *  deviceapis.pim.contact.getAddressBooks(AddressBooksCB, errorCallback);
         */
        function createContact(contactProperties) {
            ax.util.validateOptionalObjectParam(contactProperties);
            return initNewContact(undefined, contactProperties);
        }

        /**
         * <p>비동기적으로 주소록에 연락처를 추가합니다.<br>연락처가 주소록에 추가되면 id 속성 값이 부여된 Contact 객체가 매개변수로 전달되어 successCallback 함수가 호출됩니다. 연락처 추가에 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
         * •    NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * •    SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * •    INVALID_VALUES_ERR: 유효하지 않은 값이 포함된 경우 혹은 텍스트의 길이가 주소록의 제한을 넘은 경우 등<br>
         * •    UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {AddContactSuccessCallback} successCallback <p>연락처 추가에 성공한 경우 호출됩니다</p>
         * @param {ErrorCallback} errorCallback <p>연락처 추가에 실패한 경우 호출됩니다.</p>
         * @param {Contact} contact <p>단말 저장공간에 추가된 연락처 객체</p>
         * @memberOf AddressBook#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * var addressbook;
         *
         * // Define the error callback for all the asynchronous calls
         * function errorCallback(response) {
         *   alert( "The following error occurred: " +  response.code);
         * }
         *
         * function contactAddedCB(contact) {
         *   alert("Contact Added with id " + contact.id);
         * }
         *
         * // Define the success callback for retrieveing all the
         * // Address Books
         * function AddressBooksCB(addressbooks) {
         *   if(addressbooks.length > 0)
         *   {
         *     addressbook = addressbooks[0];
         *     alert("The addressbook type is " + addressbook.type +
         *           " and name " + addressbook.name);
         *
         *     var contact = addressbook.createContact(
         *                     {firstName:'Jeffrey',
         *                      lastName:'Hyman',
         *                      nicknames:['joey ramone'],
         *                      emails:[{email:'user@domain.com'}],
         *                      phoneNumbers:[{number:'666666666'}]});
         *
         *     addressbook.addContact(contactAddedCB, errorCallback, contact);
         *   }
         * }
         *
         * // Get a list of available Address Books.
         * deviceapis.pim.contact.getAddressBooks(AddressBooksCB, errorCallback);
         */
        function addContact(successCallback, errorCallback, contact) {
            if(contact === null) throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR);
            ax.util.validateRequiredFunctionParam(successCallback);

            try {
                ax.util.validateInstance(contact, Contact, true, 'contact');
            } catch (e) {
                throw new DeviceAPIError(e);
            }

            function scb(result) {
                var contact = initNewContact(result._id, result);
                ax.util.invokeLater(null, successCallback, contact);
            }
            return self.execAsyncWAC(ADD_CONTACT, scb, errorCallback, [addrBook._handle, contact]);
        }


        /**
         * <p>주소록의 기존 연락처를 비동기적으로 갱신합니다.<br>연락처 객체의 id 속성에 대응하는 연락처 정보를 업데이트 합니다. 업데이트에 성공하면 uccessCallback 함수가 호출됩니다. 업데이트에 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
         * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * •INVALID_VALUES_ERR: 유효하지 않은 값이 포함된 경우 혹은 텍스트의 길이가 주소록의 제한을 넘은 경우 등<br>
         * •NOT_FOUND_ERR: id 속성에 대응되는 연락처가 없는 경우<br>
         * •UNKNOWN_ERR: 그 밖에 다른 모든 경우
         * </p>
         *
         * @param {SuccessCallback} successCallback <p>연락처 갱신에 성공한 경우 콜백으로 호출됩니다.</p>
         * @param {ErrorCallback} errorCallback <p>연락처 갱신에 실패한 경우 콜백으로 호출됩니다.</p>
         * @param {Contact} contact <p>업데이트된 Contact 객체입니다.</p>
         * @memberOf AddressBook#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * var addressbook;
         *
         * // Define the error callback for all the asynchronous calls
         * function errorCallback(response) {
         *   alert( "The following error occurred: " +  response.code);
         * }
         *
         * function contactUpdatedCB() {
         *   // The contact has been successfully updated
         *   alert('Contact Successfully updated');
         * }
         *
         * function contactFoundCB(contacts) {
         *   // The contact has been successfully found
         *   // Let's try to add a new nickName
         *   contacts[0].nickName.push('Joey Ramone');
         *   addressbook.updateContact(contactUpdatedCB,
         *                                errorCallback,
         *                                  contacts[0]);
         * }
         *
         * // Define the success callback for retrieving all the
         * // Address Books
         * function AddressBooksCB(addressbooks) {
         *   if(addressbooks.length > 0)
         *   {
         *     addressbook = addressbooks[0];
         *     alert("The addressbook type is " + addressbook.type +
         *           " and name " + addressbook.name);
         *     addressbook.findContacts(contactFoundCB, errorCallback,
         *                                {firstName:'%Jeffrey%'});
         *   }
         * }
         *
         * // Get a list of available Address Books.
         * deviceapis.pim.contact.getAddressBooks(AddressBooksCB, errorCallback);
         */
        function updateContact(successCallback, errorCallback, contact) {
            if(contact === null) throw new DeviceAPIError(ax.TYPE_MISMATCH_ERR);
            ax.util.validateRequiredObjectParam(contact);

            try {
                ax.util.validateInstance(contact, Contact, true, 'contact');
            } catch (e) {
                throw new DeviceAPIError(e);
            }

            return self.execAsyncWAC(UPDATE_CONTACT, successCallback, errorCallback, [addrBook._handle, contact]);
        }

        /**
         * <p>주소록의 연락처를 비동기적으로 삭제합니다.<br>지정된 id 매개변수에 대응하는 연락처 정보를 삭제합니다. 연락처 삭제에 성공하면 successCallback 함수가 호출됩니다. 실패하면 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
         * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * •INVALID_VALUES_ERR: 유효하지 않은 값이 포함된 경우<br>
         * •NOT_FOUND_ERR: id 속성에 대응되는 연락처가 없는 경우<br>
         * •UNKNOWN_ERR: 그 밖에 다른 모든 경우<br>
         * </p>
         *
         * @param {SuccessCallback} successCallback <p>연락처 삭제에 성공한 경우 콜백으로 호출됩니다.</p>
         * @param {ErrorCallback} errorCallback <p>연락처 삭제에 성공한 경우 콜백으로 호출됩니다.</p>
         * @param {DOMString} id <p>삭제할 연락처의 id 속성입니다.</p>
         * @memberOf AddressBook#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * var adddressbook;
         *
         * // Define the error callback.
         * function errorCallback(response) {
         *   alert( "The following error occurred: " +  response.code);
         * }
         *
         * // Define the contact delete success callback.
         * function contactDeleteSuccessCallback() {
         *   alert("Deleted");
         * }
         *
         * // Define the contact success callback.
         * function contactSearchSuccessCallback(contacts) {
         *   // Delete the first existing contact.
         *   addressBook.deleteContact(contactDeleteSuccessCallback,
         *                             errorCallback, contacts[0].id);
         * }
         *
         * // Define the address books success callback.
         * function addressbooksSuccessCallback(addressbooks) {
         *   addressbook = addressbooks[0];
         *
         *   // Find all contacts in Address Book 0.
         *   addressbook.findContacts(contactSearchSuccessCallback,
         *                            errorCallback);
         * }
         *
         * // Get a list of available address books.
         * deviceapis.pim.contact.getAddressBooks(addressbooksSuccessCallback,
         *                                        errorCallback);
         */
        function deleteContact(successCallback, errorCallback, id) {
            ax.util.validateRequiredStringParam(id);
            return self.execAsyncWAC(DELETE_CONTACT, successCallback, errorCallback, [addrBook._handle, id + '']);
        }

        /**
         * <p>주소록에서 지정된 필터에 부합하는 연락처들에 대한 Contact 객체의 배열을 얻습니다.<br>filter 매개변수를 지정한 경우 주소록에서 ContactFilter 인터페이스에 지정한 조건에 부합하는 연락처들에 대한 Contact 객체의 배열을 매개변수로 successCallback 함수를 호출합니다. filter 매개변수를 생략한 경우 주소록의 전체 연락처에 대한 Contact 객체의 배열이 매개변수로 전달됩니다. 주소록에 연락처가 없거나 filter 조건에 부합하는 연락처가 없는 경우 빈 배열이 전달됩니다.<br>
         * 오류가 발생한 경우 오류의 원인을 설명하는 DeviceAPIError 객체를 매개변수로 하여 (errorCallback 함수를 지정한 경우) errorCallback 함수를 호출합니다. 발생 가능한 에러 코드는 다음과 같습니다.<br>
         * •NOT_SUPPORTED_ERR: 이 피쳐가 지원되지 않는 경우<br>
         * •SECURITY_ERR: 이 연산이 허용되지 않는 경우<br>
         * •INVALID_VALUES_ERR: 유효하지 않은 값이 포함된 경우<br>
         * •UNKNOWN_ERR: 그 밖에 다른 모든 경우
         * </p>
         *
         * @param {ContactArraySuccessCallback} successCallback <p>연락처 찾기에 성공한 경우 콜백으로 호출됩니다.</p>
         * @param {ErrorCallback} errorCallback <p>연락처 찾기에 실패한 경우 콜백으로 호출됩니다. </p>
         * @param {ContactFilter} filter <p>대상 연락처를 한정하기 위한 필터입니다.</p>
         * @memberOf AddressBook#
         * @type PendingOperation
         * @returns {PendingOperation} <p>비동기 방식으로 호출된 함수를 취소할 수 있는 PendingOperation 객체입니다.</p>
         * @exception <p>매개변수의 형식이 올바르지 않은 경우 TYPE_MISMATCH_ERR 오류 코드와 함께 발생됩니다.</p>
         *
         * @example
         * // Define the error callback.
         * function errorCallback(response) {
         *   alert( "The following error occurred: " +  response.code);
         * }
         *
         * // Define the contact search success callback.
         * function contactSearchSuccessCallback(contacts) {
         *    alert(contacts.length + " results found.");
         * }
         *
         * // Define the address book lists success callback.
         * function abListSuccessCallback(addressbooks) {
         *   // Find all contacts in the first address book that contains in
         *   // the nick name list the word ramone
         *   addressbooks[0].findContacts(contactSearchSuccessCallback,
         *                                errorCallback,
         *                                {nickname:"%ramone%"});
         * }
         *
         * // Get a list of available address books.
         * deviceapis.pim.contact.getAddressBooks(abListSuccessCallback,
         *                                          errorCallback);
         */
        function findContacts(successCallback, errorCallback, filter) {
            ax.util.validateRequiredFunctionParam(successCallback);
            ax.util.validateOptionalObjectParam(filter);

            function scb(result) {
                var contacts = [];

                var i;
                for (i = 0; i < result.length; i++) {
                    contacts[i] = initNewContact(result[i]._id, result[i]);
                }

                ax.util.invokeLater(null, successCallback, contacts);
            }

            return self.execAsyncWAC(FIND_CONTACTS, scb, errorCallback, [addrBook._handle, filter]);
        }

        ax.def(this)
            .constant('type', addrBook._type)
            .constant('name', addrBook._name)
            .method(CREATE_CONTACT, createContact)
            .method(ADD_CONTACT, addContact)
            .method(UPDATE_CONTACT, updateContact)
            .method(DELETE_CONTACT, deleteContact)
            .method(FIND_CONTACTS, findContacts);
    }


    ///////////////////////////////////////////////////////////////////////
    // interface Contact

    var FIRST_NAME = 'firstName';
    var MIDDLE_NAME = 'middleName';
    var LAST_NAME = 'lastName';
    var NICK_NAMES = 'nicknames';
    var PHONETIC_NAME = 'phoneticName';
    var ADDRESSES = 'addresses';
    var PHOTO_URI = 'photoURI';
    var PHONE_NUMBERS = 'phoneNumbers';
    var EMAILS = 'emails';

    /**
     * <p>이 인터페이스는 단말의 연락처를 추상화하며 ContactProperties 인터페이스를 확장합니다.</p>
     * @class 단말의 연락처를 추상화
     * @name Contact
     */
    function Contact(id) {
        // spec에 정의된 초기값을 따름
        var contact = {
            id: id,
            firstName: '',
            middleName: '',
            lastName: '',
            nicknames: [],
            phoneticName: '',
            addresses: [],        // undefined, spec과 예제 충돌
            photoURI: undefined,
            phoneNumbers: [],    // undefined, spec과 예제 충돌
            emails: []            // undefined, spec과 예제 충돌
        };

        /**
         * <p>단말 플랫폼의 연락처에 대한 식별자입니다.<br>연락처와 관련된 GUID(globally unique identifier) 값입니다. 자세한 사항은 RFC 2426, Section 3.6.6.7을 참고하십시오.<br>
         * 이 속성은 읽기 전용입니다
         * </p>
         * @memberOf Contact#
         * @name id
         * @type DOMString
         * @field
         * @example
         * // Define the error callback.
         * function errorCallback(response) {
         *   alert( "The following error occurred: " +  response.code);
         * }
         *
         * // Define the success callback.
         * function contactFindSuccessCallback(response) {
         *   alert("The first Ramone id is " + response[0].id);
         * }
         *
         * // Define the address book success callback.
         * function addressBookSuccessCallback(addressBooks) {
         *
         *   // Find all contacts in the first address book
         *   // with nick name 'Ramone'.
         *   addressBooks[0].findContacts(contactFindSuccessCallback, null,
         *                                            {nickName:"Ramone"});
         * }
         *
         *  // Get a list of available address books.
         *  deviceapis.pim.contact.getAddressBooks(addressBookSuccessCallback,errorCallback);
         */
        // interface Contact
        ax.def(this).constant('id', contact.id);

        /**
         * <p>이 인터페이스는 연락처 생성에 사용됩니다.이 인터페이스는 연락처를 생성하기 위해 createContact 함수의 매개변수로 사용됩니다.<br>
         * 모든 속성은 선택적으로 지정할 수 있으며 값을 지정하지 않은 경우 아래 설명에서 별다른 언급이 없다면 기본 값으로 undefined 값이 지정됩니다.
         * </p>
         * @class 이 인터페이스는 연락처 생성에 사용됩니다.
         * @name ContactProperties
         */
        // interface ContactProperties

        /**
         * <p>연락처의 이름.기본값은 빈 문자열입니다. RFC 2426, Section 3.1.1를 참고하십시오.</p>
         * @memberOf ContactProperties#
         * @type DOMString
         * @field
         * @name firstName
         * @example
         * var contact = addressBook.createContact();
         * contact.firstName = "Douglas Glenn";
         */
        this.__defineSetter__(FIRST_NAME, function(val) {
            ax.util.validateOptionalStringParam(val);
            contact.firstName = castAsString(val);
        });
        this.__defineGetter__(FIRST_NAME, function() { return contact.firstName; });

        /**
         * <p>연락처의 중간이름. 기본값은 빈 문자열입니다.<br>
         * Android와 iOS의 연락처 정보에는 middleName 속성이 포함되어 있습니다. 이 정보를 활용할 수 있도록 Waikiki API의 명세에 없는 Appspresso의 확장 속성을 제공합니다. 따라서 이 속성은 다른 표준 WAC 웹 런타임에서 지원되지 않습니다</p>
         * @memberOf ContactProperties#
         * @type DOMString
         * @field
         * @name middleName
         */
        this.__defineSetter__(MIDDLE_NAME, function(val) {
            ax.util.validateOptionalStringParam(val);
            contact.middleName = castAsString(val);
        });
        this.__defineGetter__(MIDDLE_NAME, function() { return contact.middleName; });

        /**
         * <p>연락처의 성.기본값은 빈 문자열입니다. RFC 2426, Section 3.1.1를 참고하십시오.</p>
         * @memberOf ContactProperties#
         * @type DOMString
         * @field
         * @name lastName
         * @example
         *   var contact = addressBook.createContact();
         *   contact.lastName = "Colvin";
         */
        this.__defineSetter__(LAST_NAME, function(val) {
            ax.util.validateOptionalStringParam(val);
            contact.lastName = castAsString(val);
        });
        this.__defineGetter__(LAST_NAME, function() { return contact.lastName; });

        /**
         * <p>연락처의 별명.기본값은 빈 배열입니다. RFC 2426, Section 3.1.1를 참고하십시오.<br>
         * ※ Appspresso의 현재 버전 1.0 beta의 iOS 런타임은 nicknames 배열에 하나의 원소만 허용합니다.</p>
         * @memberOf ContactProperties#
         * @type StringArray
         * @field
         * @name nicknames
         * @example
         * var contact = addressBook.createContact();
         * contact.nicknames.push("Dee Dee");
         */
        this.__defineSetter__(NICK_NAMES, function(val) {
            ax.util.validateOptionalArrayParam(val);
            contact.nicknames = val;
        });
        this.__defineGetter__(NICK_NAMES, function() { return contact.nicknames; });

        /**
         * <p>연락처의 발음상 이름(phonetic name). 기본값은 빈 문자열입니다.</p>
         * @memberOf ContactProperties#
         * @type DOMString
         * @field
         * @name phoneticName
         */
        this.__defineSetter__(PHONETIC_NAME, function(val) {
            ax.util.validateOptionalStringParam(val);
            contact.phoneticName = castAsString(val);
        });
        this.__defineGetter__(PHONETIC_NAME, function() { return contact.phoneticName; });

        /**
         * <p>연락처의 주소.기본값은 undefined입니다.<br>
         * 여러 개의 주소 중 기본(preference) 주소가 없으면 (‘PREF’ 타입의 주소가 없는 경우) 첫 번째 주소가 대표주소이자 기본주소입니다.
         * </p>
         * @memberOf ContactProperties#
         * @type ContactPropertiesAddressArray
         * @field
         * @name addresses
         * @example
         * var contact = addressBook.createContact();
         * var contactAddr = {streetAddress:"Gran Via, 32",
         *                   postalcode:"50013",city:"Zaragoza",country:"ES"};
         * contact.addresses[0] = contactAddr;
         */
        this.__defineSetter__(ADDRESSES, function(val) {
            ax.util.validateOptionalArrayParam(val);
            contact.addresses = val;
        });
        this.__defineGetter__(ADDRESSES, function() { return contact.addresses; });

        /**
         * <p>연락처의 사진 URL.기본값은 undefined입니다. RFC 2426, Section 3.1.4를 참고하십시오.<br>
         * ※ Appspresso 현재 버전 1.0 beta에서 이 속성에 URL을 지정해도 실제 연락처 정보에 반영되지 않습니다.
         * </p>
         * @memberOf ContactProperties#
         * @type DOMString
         * @field
         * @name photoURI
         * @example
         * var contact = addressBook.createContact();
         * contact.photoURI = "http://www.mysite.com/mypicture.jpg"; // remote picture
         */
        this.__defineSetter__(PHOTO_URI, function(val) {
            ax.util.validateOptionalStringParam(val);
            contact.photoURI = castAsString(val);
        });
        this.__defineGetter__(PHOTO_URI, function() { return contact.photoURI; });

        /**
         * <p>연락처의 전화번호. 기본값은 undefined입니다.<br>여러 개의 전화번호 중 기본(preference) 전화번호가 없으면 (‘PREF’ 타입의 전화번호가 없는 경우) 첫 번째 전화번호가 대표번호이자 기본번호입니다.</p>
         * @memberOf ContactProperties#
         * @type PhoneNumberArray
         * @field
         * @name phoneNumbers
         * @example
         * var contact = addressBook.createContact();
         * var phoneNumber = {number:"666666666"};
         * contact.phoneNumbers[0] = phoneNumber;
         */
        this.__defineSetter__(PHONE_NUMBERS, function(val) {
            ax.util.validateOptionalArrayParam(val);
            contact.phoneNumbers = val;
        });
        this.__defineGetter__(PHONE_NUMBERS, function() { return contact.phoneNumbers; });

        /**
         * <p>연락처의 전자우편 주소. 기본값은 undefined입니다.<br>여러 개의 전자우편 주소 중 기본 주소가 없으면 (‘PREF’ 타입의 전자우편 주소가 없는 경우) 첫 번째 전자우편 주소가 대표주소이자 기본주소입니다.</p>
         * @memberOf ContactProperties#
         * @type EmailAddressArray
         * @field
         * @name emails
         * @example
         * var contact = addressBook.createContact();
         * var email = {email:"deedee@ramones.com"};
         * contact.emails[0] = email;
         */
        this.__defineSetter__(EMAILS, function(val) {
            ax.util.validateOptionalArrayParam(val);
            contact.emails = val;
        });
        this.__defineGetter__(EMAILS, function() { return contact.emails; });
    }

    /**
     * <p>연락처의 주소 정보를 포함합니다.</p>
     * @class 연락처의 주소 정보를 포함합니다.
     * @name ContactAddress
     * @example
     *
     * var contact = addressBook.createContact();
     * var contactAddress = {streetAddress:"Gran Via, 32",postalcode:"50013",city:"Zaragoza",
     *                       country:"ES",types:['WORK','PREF']};
     * contact.contactAddresses[0] = contactAdress;
     */
    // spec에 모든 속성의 default 값은 undefined로 정의됨
    function ContactAddress(contact) {
        // interface Address
        this.country = contact.country || undefined;
        this.region = contact.region || undefined;
        this.county = contact.county || undefined;
        this.city = contact.city || undefined;
        this.streetAddress = contact.streetAddress || undefined;
        this.additionalInformation = contact.additionalInformation || undefined;
        this.postalCode = contact.postalCode || undefined;

        /**
         * <p>대소문자를 구분하지 않는 주소 타입의 목록. 자세한 사항은 RFC 2426, Section 3.2.1.을 참고하십시오.<br>
         * 다음 값들이 지원됩니다.<br>
         * •WORK - 직장 주소 <br>
         * •PREF - 대표 주소 <br>
         * •HOME - 집 주소 <br>
         * ※ Waikiki 명세에서는 WORK, PREF 혹은 HOME, PREF와 같이 하나의 주소에 두 개 이상의 타입을 지정할 수 있도록 되어 있으나 iOS와 Android는 주소에 대해 두 개 이상의 타입을 지정할 수 없으며 PREF 타입이 존재하지 않습니다. 앞의 예와 같이 WORK, PREF를 동시에 지정한 경우 PREF가 아닌 타입이 우선되며 PREF만 지정한 경우에는 PREF란 이름으로 사용자 정의 타입을 생성합니다. 사용자 정의된 PREF 타입은 Waikiki spec에서 의도하는 기본 값의 의미를 가지지 않습니다.
         * </p>
         * @name types
         * @filed
         * @type StringArray
         * @memberOf ContactAddress#
         */
        // interface ContactAddress
        this.types = contact.types.slice(0) || undefined;
    }


    /**
     * <p>연락처의 전화번호 정보를 포함합니다.이 인터페이스는 전화번호와 전화번호의 타입 (직장 혹은 집) 또는 장치타입 (팩스, 유선, 모바일) 등의 정보를 저장합니다.</p>
     * @class 연락처의 전화번호 정보를 포함합니다.
     * @name PhoneNumber
     *
     * @example
     * var contact = addressBook.createContact();
     * var phoneNumber = {number:"666666666",types:['WORK','VOICE']};
     * contact.phoneNumbers[0] = phoneNumber;
     */
    // spec에서 default 값에 대한 언급 없음. ContactAddress를 따라 undefined로
    function PhoneNumber(phoneNumber) {
        /**
         * <p>전화번호<br>※ Appspresso 현재 버전 1.0 beta의 iOS 런타임에서 이 값은 국가별 형식에 맞추어 저장됩니다.</p>
         * @filed
         * @type DOMString
         * @name number
         * @memberOf PhoneNumber#
         */
        this.number = phoneNumber.number || undefined;

        /**
         * <p>대소문자를 구분하지 않는 RFC 2426에 정의된 전화번호 타입입니다. 전화번호의 용도를 지정합니다.다음 값 들이 지원됩니다.<br>
         * •WORK - 직장 번호 <br>
         * •PREF - 대표 번호 <br>
         * •HOME - 집 번호 <br>
         * •VOICE - 음성 번호 (기본) <br>
         * •FAX - 팩스 번호 <br>
         * •MSG - 메시지 서비스 번호 <br>
         * •CELL - 휴대폰 번호 <br>
         * •PAGER - 호출기(삐삐) 번호 <br>
         * •BBS - BBS (게시판 서비스) 번호 <br>
         * •MODEM - 모뎀 번호 <br>
         * •CAR - 카 폰 번호 <br>
         * •ISDN - ISDM 번호 <br>
         * •VIDEO - 비디오 폰 번호 <br>
         * •PCS - PCS 번호 <br>
         * ※ Waikiki 명세에서는 WORK, VOICE와 같이 하나의 전화번호에 두 개 이상의 타입을 지정할 수 있도록 되어 있으나 iOS와 Android는 전화번호에 대해 두 개 이상의 타입을 지정할 수 없으며 PREF 타입이 존재하지 않습니다. 앞의 예와 같이 WORK, VOICE를 동시에 지정한 경우 PREF가 아닌 타입이 우선되며 PREF만 지정한 경우에는 PREF란 이름으로 사용자 정의 타입을 생성합니다. 사용자 정의된 PREF 타입은 Waikiki spec에서 의도하는 기본 값의 의미를 가지지 않습니다.<br>
         * iOS의 기본 타입과 대응되는 타입은 WORK, HOME, CELL, PAGER입니다. 나머지는 타입의 이름으로 사용자 정의 타입을 생성합니다.<br>
         * Android의 기본 타입과 대응되는 타입은 WORK, HOME, FAX, CELL, MSG, PAGER, CAR, ISDN입니다. 이 중 CELL은 MOBILE, MSG는 MMS로 대응됩니다. 나머지는 타입의 이름으로 사용자 정의 타입을 생성합니다.<br>
         * </p>
         * @filed
         * @type StringArray
         * @memberOf PhoneNumber#
         * @name types
         */
        this.types = phoneNumber.types.slice(0) || undefined;
    }




    /**
     * <p>연락처의 전자우편 주소를 포함합니다.자세한 사항은 RFC 2426, Section 3.3.2를 참고하십시오.</p>
     * @class 연락처의 전자우편 주소를 포함합니다.
     * @name EmailAddress
     * @example
     *  var contact = addressBook.createContact();
     *  var email = {email:"user@domain.com",types:['WORK','PREF']};
     *  contact.emails[0] = email;
     */
    // spec에서 default 값에 대한 언급 없음. ContactAddress를 따라 undefined로
    function EmailAddress(email) {
        /**
         * <p>전자우편 주소</p>
         * @filed
         * @type DOMString
         * @memberOf EmailAddress#
         * @name email
         */
        this.email = email.email || undefined;

        /**
         * <p>대소문자를 구분하지 않는 전자우편 주소 타입의 목록입니다.전자우편 주소의 용도를 나타냅니다.다음 값들이 지원됩니다. <br>
         * •WORK - 직장 전자우편 주소<br>
         * •PREF - 기본 전자우편 주소<br>
         * •HOME - 집 전자우편 주소<br>
         * ※ ContactAddress의 types 속성에 대한 설명과 동일합니다<br>
         * </p>
         * @filed
         * @type StringArray
         * @memberOf EmailAddress#
         * @name types
         */
        this.types = email.types.slice(0) || undefined;
    }




// ====================================================
AddressBook.prototype = ax.plugin('deviceapis.pim.contact', {
});
ax.def(AddressBook)
    .constant('SIM_ADDRESS_BOOK', SIM_ADDRESS_BOOK)
    .constant('DEVICE_ADDRESS_BOOK', DEVICE_ADDRESS_BOOK);
ax.def(AddressBook.prototype)
    .constant('SIM_ADDRESS_BOOK', SIM_ADDRESS_BOOK)
    .constant('DEVICE_ADDRESS_BOOK', DEVICE_ADDRESS_BOOK);
ContactManager.prototype = ax.plugin('deviceapis.pim.contact', {
    'getAddressBooks': getAddressBooks
});
ax.def(g)
    .constant('AddressBook', AddressBook)
    .constant('ContactManager', ContactManager);
ax.def(pim).constant('contact', new ContactManager());
// ====================================================
}(window));

/**
 * <p>지구 표면 상에서의 위치를 나타내는 속성의 집합입니다.이 인터페이스의 모든 속성의 기본값은 undefined입니다.<br>자세한 사항은 RFC 2426, Section 3.2.1를 참고하십시오.</p>
 * @class 지구 표면 상에서의 위치를 나타내는 속성의 집합입니다.
 * @name Address
 * @example
 *
 * var addressbooks = deviceapis.pim.contact.getAddressBooks();
 * // Create a contact.
 * var contact = addressbooks[0].createContact(
 *   {firstName:'Douglas Glenn', lastName:'Colvin', nicknames:['deedee'],
 *    contactAddresses:[{streetAddress:"Gran Via, 1", postalcode:"50013",
 *                     city:"Zaragoza",country:"ES"}],
 *                    emails:[{email:'user@domain.com'}]});
 */

/**
 * <p>국가.두 개의 문자로 국가를 지정하는 ISO 3166-1코드를 권고합니다.</p>
 * @memberOf Address#
 * @filed
 * @type DOMString
 * @name country
 */

/**
 * <p>주 (미국의 State 혹은 스페인의 Province)</p>
 * @memberOf Address#
 * @filed
 * @type DOMString
 * @name region
 */

/**
 * <p>주의 최대 행정구역 이름<br>※ Appspresso의 현재 버전 1.0 beta에서 지원하지 않는 속성으로 iOS, Android 모두 이 속성을 제공하지 않습니다.</p>
 * @memberOf Address#
 * @filed
 * @type DOMString
 * @name county
 */

/**
 * <p>도시</p>
 * @memberOf Address#
 * @filed
 * @type DOMString
 * @name city
 */

/**
 * <p>번지</p>
 * @memberOf Address#
 * @filed
 * @type DOMString
 * @name streetAddress
 */

/**
 * <p>상세주소로서 예를 들면 건물의 층 수, 아파트 동 번호, 사무실 이름 등입니다.<br>
 * ※ Appspresso의 현재 버전 1.0 beta에서 지원하지 않는 속성으로 iOS, Android 모두 이 속성을 제공하지 않습니다.</p>
 * @memberOf Address#
 * @filed
 * @type DOMString
 * @name additionalInformation
 */

/**
 * <p>우편 번호</p>
 * @memberOf Address#
 * @filed
 * @type DOMString
 * @name postalCode
 */



/**
 * <p>findContacts 함수에서 반환하는 연락처를 제한하기 위한 필터입니다.<br>
 * findContacts 함수에 이 필터를 전달하면 주소록에서 이 필터에 부합하는 연락처만 필터링할 수 있습니다. 검색은 SQL의 “AND” 연산과 유사한 방식으로 수행됩니다.<br>
 * 다음의 규칙에 따라 필터링을 수행합니다.<br>
 * •문자열 형식의 필터 속성의 경우 U+0025 'PERCENT SIGN' 와일드카드 문자(열)를 포함하지 않는 한 필터의 속성과 연락처의 속성이 정확하게 일치해야 합니다. 와일드카드 문자가 사용되면 SQL의 “LIKE” 조건과 유사한 방식으로 수행됩니다. (%는 빈 문자열을 포함한 어떤 문자열과도 대응됩니다.)
 * </p>
 * @class findContacts 함수에서 반환하는 연락처를 제한하기 위한 필터
 * @name ContactFilter
 */

/**
 * <p>연락처의 id 속성의 필터링에 사용됩니다.이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) id 속성을 갖는 연락처는 필터 기준을 만족합니다.<br>※ Appspresso의 현재 버전 1.0 beta에서는 id 속성에 대해 와일드카드를 지원하지 않습니다.</p>
 * @filed
 * @type DOMString
 * @memberOf ContactFilter#
 * @name id
 */

/**
 * <p>연락처의 firstName 속성의 필터링에 사용됩니다.이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) firstName 속성을 갖는 연락처는 필터 기준을 만족합니다. </p>
 * @filed
 * @type DOMString
 * @memberOf ContactFilter#
 * @name firstName
 */

/**
 * <p>연락처의 lastName 속성의 필터링에 사용됩니다.이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) lastName속성을 갖는 연락처는 필터 기준을 만족합니다. </p>
 * @filed
 * @type DOMString
 * @memberOf ContactFilter#
 * @name lastName
 */

/**
 * <p>연락처의 phoneticName 속성의 필터링에 사용됩니다.이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) pheneticName속성을 갖는 연락처는 필터 기준을 만족합니다. </p>
 * @filed
 * @type DOMString
 * @memberOf ContactFilter#
 * @name phoneticName
 */

/**
 * <p>연락처의 nickNames 속성의 필터링에 사용됩니다.nickNames 배열의 원소들 중 하나 이상이 이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) 연락처는 필터 기준을 만족합니다. </p>
 * @filed
 * @type DOMString
 * @memberOf ContactFilter#
 * @name nickName
 */

/**
 * <p>연락처의 phoneNumbers 속성의 필터링에 사용됩니다.phoneNumbers 배열의 원소들 중 하나 이상이 이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) 연락처는 필터 기준을 만족합니다</p>
 * @filed
 * @type DOMString
 * @memberOf ContactFilter#
 * @name phoneNumber
 */

/**
 * <p>연락처의 emails 속성의 필터링에 사용됩니다.emails 배열의 원소들 중 하나 이상이 이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) 연락처는 필터 기준을 만족합니다.</p>
 * @filed
 * @type DOMString
 * @memberOf ContactFilter#
 * @name emails
 */

/**
 * <p>연락처의 addresses 속성의 필터링에 사용됩니다.addresses 배열의 원소들 중 하나 이상이 이 속성과 일치하는(정확히 일치 혹은 와일드카드로 일치) 연락처는 필터 기준을 만족합니다.<br>
 * ※ Appspresso의 현재 버전 1.0 beta에서 Android 런타임은 이 속성을 이용한 검색을 지원하지 않습니다.
 * </p>
 * @filed
 * @type Address
 * @memberOf ContactFilter#
 * @name address
 */




/**
 * <p>연락처 목록을 얻기 위한 콜백 함수를 정의합입니다.</p>
 * @namespace
 * @name ContactArraySuccessCallback
 */

/**
 * <p>연락처 목록을 얻는 데 성공한 경우 호출됩니다.</p>
 * @param {ContactArray} contacts <p>연락처의 목록</p>
 * @return {void}
 * @function
 * @memberOf ContactArraySuccessCallback#
 * @name onsuccess
*/

/**
 * <p>주소록에 연락처를 추가하기 위해 사용되는 성공 콜백 함수를 정의합니다. 이 콜백 인터페이스는 매개변수로 주소록에 추가된 Contact 객체를 전달받는 성공 콜백 함수를 정의합니다.</p>
 * @namespace
 * @name AddContactSuccessCallback
 */

/**
 * <p>연락처 추가에 성공한 경우 콜백으로 호출됩니다.</p>
 * @function
 * @param {Contact} contacts <p>연락처의 목록</p>
 * @return {void}
 * @memberOf AddContactSuccessCallback
 * @name onsuccess
*/

/**
 * <p>주소록 목록을 얻기 콜백 함수를 정의합니다. 이 콜백 인터페이스는 AddressBook 객체의 배열을 매개변수로 전달받는 성공 콜백 함수를 정의합니다. 이 콜백 함수는 비동기적으로 주소록 목록을 얻기 위해 사용됩니다.</p>
 * @namespace
 * @name AddressBookArraySuccessCallback
 */

/**
 * <p>주소록 목록을 얻는 데 성공한 경우 호출됩니다.</p>
 * @function
 * @param {AddressBookArray} addressbooks <p>주소록의 목록</p>
 * @return {void}
 * @memberOf AddressBookArraySuccessCallback
 * @name onsuccess
*/

