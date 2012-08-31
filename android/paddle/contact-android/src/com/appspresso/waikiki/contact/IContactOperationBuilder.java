package com.appspresso.waikiki.contact;

import java.util.ArrayList;
import com.appspresso.waikiki.contact.data.IContact;
import android.content.ContentProviderOperation;

interface IContactOperationBuilder {
    ArrayList<ContentProviderOperation> build(IContact contact);
}
