var eclipseTesting = AsyncTestCase('eclipseTesting');

eclipseTesting.prototype.test_with_eclipse_external_tool = function(queue) {

};

var fsAsync = AsyncTestCase('fsAsync');

fsAsync.prototype.text_updateContact = function(queue){
    var scb, ecb, addressBook;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: getAddressBooks', function(callbacks) {
        scb = callbacks.add(function(addressbooks){
            addressBook = addressbooks[0];
        });
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getAddressbook_ok.json';
        deviceapis.pim.contact.getAddressBooks(scb, ecb);
    });

    queue.call('Step 1: updateContact', function(callbacks) {
        console.log('================== Step 1: updateContact ======================================================');

        var scb_update = function(){};
        var ecb_update = function(e){};

        scb = function(contacts){

            try{
                addressBook.updateContact(scb_update, ecb_update, null);
            }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-1 updateContact with invalid param <<null>>:", e.message, e.code); };

            try{
                addressBook.updateContact(scb_update, ecb_update, {});
            }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-2 updateContact with invalid param <<{}>>:", e.message, e.code); };

            try{
                addressBook.updateContact(scb_update, ecb_update, []);
            }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-3 updateContact with invalid param <<[]>>:", e.message, e.code); };

            var nicknames = contacts[0].nicknames;
            nicknames.push("Dee Dee");
            contacts[0].nicknames = nicknames;

            _APPSPRESSO_REQUEST_URL = '/test/src-test/general_ok.json';
            var scb_update = function(){
                console.log("OK 1-4 updateContact successed!");
            };

            addressBook.updateContact(scb_update, ecb_update, contacts[0]);
        };


        _APPSPRESSO_REQUEST_URL = '/test/src-test/findContacts_ok.json';
        addressBook.findContacts(scb, ecb);

    });

};

fsAsync.prototype.test_findContacts = function(queue){
    var scb, ecb, addressBook;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: getAddressBooks', function(callbacks) {
        scb = callbacks.add(function(addressbooks){
            addressBook = addressbooks[0];
        });
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getAddressbook_ok.json';
        deviceapis.pim.contact.getAddressBooks(scb, ecb);
    });

    queue.call('Step 1: findContacts with filter', function(callbacks) {
        console.log('================== Step 1: findContacts with filter ======================================================');

        var filter;
        scb = function(contacts){
            console.log("OK 1-5 findContacts with filter successed!");
        };

        try{
            addressBook.findContacts(scb, ecb, null);    //TODO: 확인필요 null 입력시  success?
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-1 findContacts with invalid filter <<null>>:", e.message, e.code); };

        try{
            addressBook.findContacts(scb, ecb, []);        //TODO: 확인필요 array 입력시  success?
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-2 findContacts with invalid filter <<[]>>:", e.message, e.code); };

        try{
            addressBook.findContacts(scb, ecb, 'hello');
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-3 findContacts with invalid filter <<hello>>:", e.message, e.code); };

        try{
            addressBook.findContacts(scb, ecb, 123);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-4 findContacts with invalid filter <<123>>:", e.message, e.code); };

        var filter = {};

        _APPSPRESSO_REQUEST_URL = '/test/src-test/findContacts_ok.json';
        addressBook.findContacts(scb, ecb, filter);

    });
}

fsAsync.prototype.test_deleteContact = function(queue){
    var scb, ecb, addressBook;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: getAddressBooks', function(callbacks) {
        scb = callbacks.add(function(addressbooks){
            addressBook = addressbooks[0];
        });
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getAddressbook_ok.json';
        deviceapis.pim.contact.getAddressBooks(scb, ecb);
    });

    queue.call('Step 1: deleteContact', function(callbacks) {
        console.log('================== Step 1: deleteContact ======================================================');


        var scb_delete = function(){};
        var ecb_delete = function(e){};

        scb = function(contacts){

            try{
                addressBook.deleteContact(scb_delete, ecb_delete, null);
            }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-1 deleteContact with invalid param <<null>>:", e.message, e.code); };

            try{
                addressBook.deleteContact(scb_delete, ecb_delete, undefined);
            }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-2 deleteContact with invalid param <<undefined>>:", e.message, e.code); };

            try{
                addressBook.deleteContact(scb_delete, ecb_delete, 123);
            }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-3 deleteContact with invalid param <<123>>:", e.message, e.code); };

            _APPSPRESSO_REQUEST_URL = '/test/src-test/general_ok.json';
            var scb_delete = function(){
                console.log("OK 1-4 deleteContact successed!");
            };
            addressBook.deleteContact(scb_delete, ecb_delete, contacts[0].id);
        };


        _APPSPRESSO_REQUEST_URL = '/test/src-test/findContacts_ok.json';
        addressBook.findContacts(scb, ecb);

    });
};

fsAsync.prototype.test_addContact = function(queue){
    var scb, ecb, addressBook;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 0: getAddressBooks', function(callbacks) {
        scb = callbacks.add(function(addressbooks){
            addressBook = addressbooks[0];
        });
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getAddressbook_ok.json';
        deviceapis.pim.contact.getAddressBooks(scb, ecb);
    });

    queue.call('Step 1: createContact', function(callbacks) {
        console.log('================== Step 1: createContact ======================================================');


          var contact = addressBook.createContact({firstName:'Jeffrey',lastName: 'Young'});

        /*
          var contact = addressBook.createContact(
                          {firstName:'Jeffrey',
                           lastName: 'Young',
                           emails:[{email:'user@domain.com'}],
                           phoneNumbers:[{number:'666666666'}]});
        */

        scb = function(o){
                assertSame(' firstName', 'Jeffrey', o.firstName);
                assertSame(' middleName', '', o.middleName);
                assertSame(' lastName', 'Young', o.lastName);
                assertSame(' phoneticName', '', o.phoneticName);
                assertSame(' nicknames', typeof [], typeof o.nicknames);
                assertSame(' addresses', undefined, o.addresses);
                assertSame(' emails', undefined, o.emails);
                assertSame(' phoneNumbers', undefined, o.phoneNumbers);
                assertSame(' photoURI', null, o.photoURI);
                console.log('OK 1-1 createContact successed:');
                for(var i in o){
                    console.log(i + ' ' + typeof o[i] + ' "' + o[i] + '"');
                }
        };

        try{
            addressBook.addContact(scb, ecb, null);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-1 addContact with invalid param <<null>>:", e.message, e.code); };

        try{
            addressBook.addContact(scb, ecb, {});
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-2 addContact with invalid param <<{}>>:", e.message, e.code); };

        try{
            addressBook.addContact(scb, ecb, []);
        }catch(e){assertSame('TYPE_MISMATCH_ERR', 17, e.code); console.log("OK 1-3 addContact with invalid param <<[]>>:", e.message, e.code); };

        _APPSPRESSO_REQUEST_URL = '/test/src-test/addContact_ok.json';
        addressBook.addContact(scb, ecb, contact);

    });

}
fsAsync.prototype.test_getAddressBooks = function(queue){

    var scb, ecb, addressBook;
    _APPSPRESSO_REQUEST_METHOD = 'GET';

    queue.call('Step 1: getAddressBooks', function(callbacks) {
        console.log('================== Step 1: getAddressBooks ======================================================');

        var contact = deviceapis.pim.contact;
        assertObject('deviceapis.pim.contact obj', contact);

        try {
            contact.getAddressBooks(scb, ecb);
        } catch(e) {
            console.log("OK getAddressBooks with <<undefined>> scb", e.message, e.code);
            assertSame('TYPE_MISMATCH_ERR', 17, e.code);
        }

        scb = function(addressbooks){
            addressBook = addressbooks[0];

            assertFunction('createContact', addressBook.createContact);
            assertFunction('addContact', addressBook.addContact);
            assertFunction('updateContact', addressBook.updateContact);
            assertFunction('deleteContact', addressBook.deleteContact);
            assertFunction('findContacts', addressBook.findContacts);

            console.log("OK getAddressBooks scb");
        };
        _APPSPRESSO_REQUEST_URL = '/test/src-test/getAddressbook_ok.json';
        contact.getAddressBooks(scb, ecb);
    });

};

