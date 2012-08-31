package com.appspresso.waikiki.contact;

import android.os.Build;
import com.appspresso.api.fs.AxFileSystemManager;

public class AddressBookFactory {
    public static IAddressBook createAddressBook(int type, AxFileSystemManager fileSystemManager) {
        switch (type) {
            case IAddressBook.DEVICE_ADDRESS_BOOK:

                if (Build.VERSION.SDK_INT < 14) {
                    return new DeviceAddressBook(0, fileSystemManager);
                }
                else {
                    return new DeviceAddressBookAPILevel14(0, fileSystemManager);
                }
        }

        return null;
    }
}
