package com.appspresso.waikiki.filesystem;

import java.util.Map;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileFilter;

public class WkFileFilter implements AxFileFilter {
    public static final String NAME = "name";
    public static final String START_MODIFIED = "startModified";
    public static final String END_MODIFIED = "endModified";
    public static final String WILD_CARD = "%";

    private final String name;
    private final long startModified;
    private final long endModified;

    public WkFileFilter(Map<Object, Object> map) {
        Object value = map.get(NAME);
        name = (value instanceof String) ? (String) value : null;

        value = map.get(START_MODIFIED);
        startModified = (value instanceof Long && (Long) value > 0) ? (Long) value : 0;

        value = map.get(END_MODIFIED);
        endModified =
                (value instanceof Long && (Long) value > 0) ? (Long) value : System
                        .currentTimeMillis();
    }

    @Override
    public boolean acceptFile(AxFile file) {
        if (!filteringName(name, file.getName(), WILD_CARD)) return false;
        if (!filteringModified(startModified, endModified, file.getModified())) return false;

        return true;
    }

    private boolean filteringName(String name, String compareName, String wildCard) {
        if (name == null) return true;

        String[] tokens = name.split(wildCard);
        int length = tokens.length;
        int index = -1;

        for (int i = 0; i < length; i++) {
            index = compareName.indexOf(tokens[i]);
            if (index == -1) return false;
            compareName = compareName.substring(index + tokens[i].length());
        }

        return true;
    }

    private boolean filteringModified(long startModified, long endModified, long compareModified) {
        return startModified <= compareModified && endModified >= compareModified;
    }
}
