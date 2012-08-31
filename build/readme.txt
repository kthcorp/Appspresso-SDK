Appspresso Build
================

prerequisites
-------------

* java 1.6+
* ant 1.8+
* android sdk r20
* xcode 4+(mac osx only)

how to build
------------
    $ export ANDROID_HOME='android sdk dir'
    $ export IOS_HOME='xcode dir'
    $ cd build
    $ ant

files
-----
    /build.xml                     .... Appspresso Build Script
    /build-common.xml              .... Common Module Build Script
    /build-anchor.xml              .... Appspresso Build Script Module(a.k.a Anchor) Build Script
    /build-android-runtime.xml     .... Android Runtime Build Script
    /build-ios-runtime.xml         .... iOS Runtime Build Script
    /build-plugins.xml             .... Plugin Build Script
    /build-support-java.xml        .... Java Module Build Support Script
    /build-support-js.xml          .... Javascript Module Build Support Script
    /build-support-objc.xml        .... Objective-C Module Build Support Script
    /copyright.txt                 .... license notice header(for public source code)
    /tools                         .... build-time tools/libraries
