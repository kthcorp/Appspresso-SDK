package com.appspresso.screw.media;

public interface ResultObserver {
    void success(Object result);

    void error(int code, String message);
}
