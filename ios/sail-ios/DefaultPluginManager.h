//
//  DefaultPluginManager.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "PluginManager.h"

@class DefaultWidgetAgent;

/**
 * 플러그인 관리.
 *
 * TODO: rename to DefaultPluginManager
 * TODO: ... remove unnecessary static & global functions and variables
 * TODO: 웹서버 관련 로직 분리.
 *
 * @see 안드로이드 런타임의 PluginManager/DefaultPluginManager
 */
@interface DefaultPluginManager : NSObject<PluginManager,UIApplicationDelegate,NSXMLParserDelegate> {
@private    
    DefaultWidgetAgent *_widgetAgent;
    NSMutableDictionary *_pluginClassName;
    NSMutableDictionary *_pluginInstances;
    NSMutableDictionary *_pluginForFeature;
    NSMutableArray *_pluginLoadedOrder;
    NSString *_currentPluginId;
    

//    NSMutableArray *_pluginFeatures;
//    NSMutableDictionary *_createdPluginModules;
}

- (id)initWithWidgetAgent:(DefaultWidgetAgent*)widgetAgent;
//
//// Objects
//- (id)createdPluginObjectForName:(NSString *)name;
//- (void)loadAllPluginModules;
//
//// Plugin releated info.
//- (void)setPluginFeatures:(NSArray *)features;
//- (NSArray *)pluginFeatures;
//
//- (NSArray *)moduleNames;
//- (NSString *)moduleNameForFeature:(NSString *)feature;
//- (NSString *)moduleNameForScript:(NSString *)script;
//- (NSString *)moduleNameForNamespace:(NSString *)ns;
//- (NSString *)scriptForFeature:(NSString *)feature;
@end
