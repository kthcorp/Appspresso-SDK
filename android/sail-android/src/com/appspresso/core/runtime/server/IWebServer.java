package com.appspresso.core.runtime.server;

import com.appspresso.api.activity.ActivityListener;

/**
 * This interface defines features for the built-in webserver.
 * 
 * TODO: how to enforce essential context handlers such as an asset resource handler, a jsonrpc
 * handler et all?
 */
public interface IWebServer extends ActivityListener {

    String getHost();

    int getPort();

}
