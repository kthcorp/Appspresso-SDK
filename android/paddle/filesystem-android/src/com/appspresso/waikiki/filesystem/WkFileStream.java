package com.appspresso.waikiki.filesystem;

import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginResult;

public interface WkFileStream extends AxPluginResult {
    /**
     * @throws AxError
     */
    public void close() throws AxError;

    /**
     * @return
     * @throws AxError
     */
    long getBytesAvailable() throws AxError;

    /**
     * @return
     * @throws AxError
     */
    long getPosition() throws AxError;

    /**
     * @param position
     * @throws AxError
     */
    void setPosition(long position) throws AxError;

    /**
     * @return
     * @throws AxError
     */
    boolean isEof() throws AxError;

    /**
     * @return
     */
    public long getHandle();
}
