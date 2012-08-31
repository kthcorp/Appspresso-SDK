package com.appspresso.waikiki.contact;

import java.util.HashMap;
import java.util.Map;

import com.appspresso.api.AxPluginResult;

abstract class AbstractAddressBook implements IAddressBook, AxPluginResult {

    protected final int type;
    protected final String name;
    protected final int handle;

    public AbstractAddressBook(int type, String name, int handle) {
        this.type = type;
        this.name = name;
        this.handle = handle;
    }

    @Override
    public Object getPluginResult() {
        Map<String, Object> res = new HashMap<String, Object>(3);
        res.put("_handle", handle);
        res.put("_type", type);
        res.put("_name", name);

        return res;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getHandle() {
        return this.handle;
    }
}
