//
//  AxApplicationDelegate.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>

@class MulticastApplicationDelegate;
@class AxViewController;
@class DefaultWidgetAgent;

/**
 * iOS 런타임의 엔트리 포인트.
 * 
 * UIApplicationDelegate 멀티캐스팅.
 *
 * 최종 앱의 AppDelegate가 이 녀석을 상속받으면 땡~
 *
 * @see sunny의 SunnyAppDelegate
 * @see android의 WidgetActivity/WidgetAgent/DefaultWidgetAgent
 */
@interface AxApplicationDelegate : NSObject<UIApplicationDelegate> {
@private
    MulticastApplicationDelegate *_multicastApplicationDelegate;
    UIWindow *_window;
    AxViewController *_viewController;
    DefaultWidgetAgent *_widgetAgent;
}

@property (nonatomic, retain, readonly) MulticastApplicationDelegate *multicastApplicationDelegate;
@property (nonatomic, retain) UIWindow *window;
@property (nonatomic, retain, readonly) AxViewController *viewController;
@property (nonatomic, retain, readonly) DefaultWidgetAgent* widgetAgent;

@end
