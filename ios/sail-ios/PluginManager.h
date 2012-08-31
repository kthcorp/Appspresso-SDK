//
//  PluginManager.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

@protocol AxPlugin;

/**
 * 플러그인 관리자.
 *
 * @see 안드로이드 런타임의 PluginManager
 */
@protocol PluginManager

-(id<AxPlugin>)requirePlugin:(NSString*)pluginId;
-(id<AxPlugin>)requirePluginWithFeature:(NSString*)featureId;
-(NSArray*)getPluginLoadedOrder;//XXX:????

@end
