//
//  WidgetAgent.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class AxViewController;
@protocol AxFileSystemManager;
@protocol PluginManager;
@protocol W3Widget;
@protocol AxRuntimeContext;

/**
 * 위젯 하나당 인스턴스 하나씩~
 *
 * NOTE: 현재로썬 앱 하나 당 위젯 인스턴스가 하나 밖에 없지만...
 *
 * @see 안드로이드 런타임의 WidgetAgent
 */
@protocol WidgetAgent

-(AxViewController*)getViewController;
-(UIWebView*)getWebView;
-(id<AxFileSystemManager>)getFileSystemManager;
-(id<PluginManager>)getPluginManager;
-(id<W3Widget>)getWidget;
-(id<AxRuntimeContext>)getAxRuntimeContext;
-(NSString*)getBaseDir;

@end
