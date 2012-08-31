//
//  WidgetContentHandler.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

#import "AppspressoResponse.h"

/**
 * 위젯 컨텐츠(assets/ax_www 의 내용)를 제공하는 핸들러.
 *
 */
@interface WidgetContentHandler : AppspressoResponse
{
@private
	NSMutableArray *userAgentLocales;
}

- (NSData*)findWidgetContents:(NSString *)path;

- (void) deriveUserAgentLocales;

@end
