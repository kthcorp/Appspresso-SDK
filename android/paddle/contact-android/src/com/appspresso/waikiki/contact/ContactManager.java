package com.appspresso.waikiki.contact;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.waikiki.contact.data.ContactBuilder;
import com.appspresso.waikiki.contact.data.IContact;
import com.appspresso.waikiki.contact.photofilesystem.DevicePhotoFileSystem;

final public class ContactManager extends DefaultAxPlugin {
    private static final Log Log = AxLog.getLog(ContactManager.class.getSimpleName());

    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/pim.contact";
    public static final String FEATURE_READ = "http://wacapps.net/api/pim.contact.read";
    public static final String FEATURE_WRITE = "http://wacapps.net/api/pim.contact.write";

    private Map<Integer, IAddressBook> addressBooks;
    private boolean canRead;
    private boolean canWrite;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        runtimeContext.requirePlugin("deviceapis.pim");

        initPermission();
        mountPhotoFileSystems();
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        unmountPhotoFileSystems();

        super.deactivate(runtimeContext);
    }

    private void initPermission() {
        canRead =
                runtimeContext.isActivatedFeature(FEATURE_DEFAULT)
                        || runtimeContext.isActivatedFeature(FEATURE_READ);
        canWrite =
                runtimeContext.isActivatedFeature(FEATURE_DEFAULT)
                        || runtimeContext.isActivatedFeature(FEATURE_WRITE);
        if (!canRead && !canWrite) { throw new AxError(AxError.SECURITY_ERR, ""); }
    }

    private void mountPhotoFileSystems() {
        Properties options = new Properties();
        options.put(DevicePhotoFileSystem.PROPERTY_CONTEXT, runtimeContext.getActivity());
        runtimeContext.getFileSystemManager().mount(DeviceAddressBook.PHOTO_FILE_SYSTEM_PREFIX,
                new DevicePhotoFileSystem(), options);
    }

    private void unmountPhotoFileSystems() {
        runtimeContext.getFileSystemManager().unmount(DeviceAddressBook.PHOTO_FILE_SYSTEM_PREFIX);
    }

    public void getAddressBooks(AxPluginContext context) {
        if (this.addressBooks == null) {
            this.addressBooks = new HashMap<Integer, IAddressBook>(2);

            IAddressBook addressbook =
                    AddressBookFactory.createAddressBook(IAddressBook.DEVICE_ADDRESS_BOOK,
                            runtimeContext.getFileSystemManager());
            this.addressBooks.put(addressbook.getHandle(), addressbook);
        }

        context.sendResult(this.addressBooks.values().toArray());
    }

    private IAddressBook getAddressBook(int type) {
        return addressBooks.get(type);
    }

    public void addContact(AxPluginContext context) {
        if (!canWrite) {
            context.sendError(AxError.SECURITY_ERR, ""); // TODO Error message
            return;
        }

        int type = context.getParamAsNumber(0).intValue();

        @SuppressWarnings("unchecked")
        IContact contact =
                ContactBuilder.getBuilder().createContactFromMap(
                        (Map<String, Object>) (context.getParams()[1]));
        IContact res = getAddressBook(type).addContact(runtimeContext.getActivity(), contact);

        if (res == null) { throw new AxError(AxError.IO_ERR); }

        if (Log.isTraceEnabled()) {
            Log.trace("AddContact : new id - " + res.getId());
        }
        context.sendResult(res);
    }

    public void updateContact(AxPluginContext context) {
        if (!canWrite) {
            context.sendError(AxError.SECURITY_ERR, ""); // TODO Error message
            return;
        }

        int type = context.getParamAsNumber(0).intValue();

        @SuppressWarnings("unchecked")
        IContact contact =
                ContactBuilder.getBuilder().createContactFromMap(
                        (Map<String, Object>) (context.getParams()[1]));

        if (contact.getId() == null) { throw new AxError(AxError.INVALID_VALUES_ERR,
                "contact.id is null"); }

        getAddressBook(type).updateContact(runtimeContext.getActivity(), contact);
        context.sendResult();
    }

    public void deleteContact(AxPluginContext context) {
        if (!canWrite) {
            context.sendError(AxError.SECURITY_ERR, ""); // TODO Error message
            return;
        }

        int type = context.getParamAsNumber(0).intValue();
        String id = context.getParamAsString(1);

        getAddressBook(type).deleteContact(runtimeContext.getActivity(), id);
        context.sendResult();
    }

    public void findContacts(AxPluginContext context) {
        if (!canRead) {
            context.sendError(AxError.SECURITY_ERR, ""); // TODO Error message
            return;
        }

        int type = context.getParamAsNumber(0).intValue();
        Map<String, Object> filter = context.getParamAsMap(1);
        IContact[] contacts =
                getAddressBook(type).findContacts(runtimeContext.getActivity(), filter);
        context.sendResult(contacts);
    }
}
