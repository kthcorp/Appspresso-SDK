package com.appspresso.core.runtime.plugin;

abstract class AxPluginXmlParserFactory {

    static AxPluginXmlParser newAxPluginXmlParser() {
        return new DefaultAxPluginXmlParser();
    }
}
