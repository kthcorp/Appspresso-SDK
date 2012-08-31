package com.appspresso.waikiki.contact;

import java.util.ArrayList;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.provider.ContactsContract;
import com.appspresso.api.AxError;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.waikiki.contact.data.IContact;

public class DeviceAddressBookAPILevel14 extends DeviceAddressBook {
    public DeviceAddressBookAPILevel14(int handle, AxFileSystemManager fileSystemManager) {
        super(handle, fileSystemManager);
    }

    @Override
    public IContact addContact(Context context, IContact contact) {
        // 연락처 추가시 특정 ACCOUNT_NAME, ACCOUNT_TYPE에 대해서 UNGROUNPED_VISIBLE을 1로
        // 지정하는 것을 제외하도록 한다.
        ArrayList<ContentProviderOperation> ops =
                OperationBuilder.getAddContactOperationBuilder().build(contact);

        ContentProviderResult[] cpr = null;
        try {
            cpr = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (cpr == null || cpr.length < 1) { return null; }

            String id = ContactIDEncoder.encode((int) ContentUris.parseId(cpr[0].uri));
            return getContacts(context.getContentResolver(), new int[] {Integer.valueOf(id)})
                    .get(0);
        }
        catch (Exception e) {
            throw new AxError(AxError.UNKNOWN_ERR, "addContact operation failed");
        }
    }
}
