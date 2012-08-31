package com.appspresso.core.runtime.filesystem;

public interface AxUriCodec {
    String encode(String path);

    String decode(String uri);
}
