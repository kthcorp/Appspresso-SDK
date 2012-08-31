//
//  DefaultRuntimeContext.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "AxRuntimeContext.h"

@class DefaultWidgetAgent;

/**
 * 위젯 인스턴스(WidgetAgent)가 외부 세계(플러그인...)와 주고받는 정보들...
 *
 * @see 안드로이드 런타임...
 */
@interface DefaultRuntimeContext : NSObject <AxRuntimeContext> {
@private
    DefaultWidgetAgent* _widgetAgent;
    NSMutableDictionary *_attrs;
    NSDictionary *_launchOptions;
}

@property (nonatomic, assign) NSDictionary *launchOptions;
-(id)initWithWidgetAgent:(DefaultWidgetAgent*)wigetAgent;

@end
