package com.appspresso.waikiki.contact.photofilesystem;

import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileSystem;

public interface PhotoFileSystem extends AxFileSystem {
    static final Log L = AxLog.getLog(PhotoFileSystem.class);

    /**
     * Raw ID의 AxFile을 반환한다.
     * 
     * @param rawId
     * @return AxFile
     */
    AxFile getFileByRawId(long rawId);

    /**
     * Contact ID의 AxFile를 반환한다. 다음과 같은 동작을 수행한다.
     * 
     * AxFile axFile = getFile(Long.parse(contactId));
     * 
     * @param contactId
     * @return AxFile
     */
    AxFile getFileByContactId(long contactId);
}
