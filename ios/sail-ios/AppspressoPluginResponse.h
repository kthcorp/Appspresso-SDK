//
//  AppspressoPluginResponse.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "AppspressoResponse.h"

#define kApiKeyPlugin @"/appspresso/plugin/"
#define kApiKeyRpcPoll @"/appspresso/rpcpoll/"

@class DefaultPluginManager;

/**
 * /appspress/plugin으로 POST요청을 받아서 해당 플러그인의 exec을 호출.
 *
 * @see 안드로이드 런타임의 JsonRpcHandler
 */
@interface AppspressoPluginResponse : AppspressoResponse {
    NSDictionary* _queryParamsMap;
}

@end
