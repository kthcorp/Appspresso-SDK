Prerequisite
==========================

* Java 1.6+
* Apache Ant 1.8+
* Android SDK
 * Android SDK Tools 20+
 * Android SDK Platform-tools 12+
 * SDK Platform
* Xcode 4.x+ (only OSX)

Building
=============================
Setting Environment Variables
-----------------------------
MAC :

    $ export ANDROID_HOME=<Android SDK Path>
    $ export IOS_HOME=<Xcode Path>

e.g. 

    $ export ANDROID_HOME=/devel/android-sdk-mac_x86/
    $ export IOS_HOME=/Developer/                                  #Xcode 4.2-
    $ export IOS_HOME=/Applications/Xcode.app/Contents/Developer/  #Xcode 4.3+

Windows :

    $ set ANDROID_HOME=<Android SDK Path>

e.g. 

    $ set PATH=%PATH%;<Ant Path>/bin;    # If you need
    $ set ANDROID_HOME="C:\Program Files\Android\android-sdk"

Build
-----------------------------
    $ cd sdk/build
    $ ant

Using Appspresso SDK with Studio
=============================

1. Run Appspresso Studio
1. [Preferences] - [Appspresso]
1. Check 'Use custom Appspresso SDK Home'
1. Set Appspresso SDK directory (e.g. \<Appspresso SDK Project\>/build/output/sdk)

Directory Layout
=============================
    
    /
        /build                      ... Build script for Appspresso SDK
        /common
            /keel                   ... Javascript API
            /anchor                 ... Ant script for Appspresso app and plugin
            /reship                 ... Migration tool
        /android
            /chronometer-android    ... Public API/SPI(Service Provider Interface) for Android
            /sail-android           ... Appspresso Runtime for Android
            /merry                  ... Appspresso Application for Android
            /paddle                 ... WAC API Android Module Project. Peer of motor
                /...
            /screw                  ... Appspresso Extension API Android Module Project. Peer of rower
                /...
        /ios
            /chronometer-ios        ... Public API/SPI(Service Provider Interface) for iOS
            /sail-ios               ... Appspresso Runtime for iOS
            /sunny                  ... Appspresso Application for iOS
            /paddle                 ... WAC API iOS Module. Peer of motor
                /...
            /screw                  ... Appspresso Extension API iOS Module Project. Peer of rower
                /...
        /plugins
            /motor                  ... WAC API(deviceapis) Plugin Project. Peer of paddle
            /rower                  ... Appspresso Extension API(ax.ext) Plugin Project. Peer of screw

Site
=============================

* [Hompage](http://www.appspresso.com/)
* [API Reference](http://appspresso.com/api-reference)
=============================
