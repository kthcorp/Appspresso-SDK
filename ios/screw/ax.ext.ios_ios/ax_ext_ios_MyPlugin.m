//
//  ax_ext_ios_MyPlugin.m
//  ax.ext.ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxError.h"
#import "AxPluginContext.h"
#import "AxRuntimeContext.h"

#import "ax_ext_ios_MyPlugin.h"


@implementation ax_ext_ios_MyPlugin

- (void)finish:(id<AxPluginContext>)context
{
	//AX_LOG_TRACE(@"%s: params=%@", __PRETTY_FUNCTION__, params);
	int exitCode = [context getParamAsInteger:0];

	[context sendResult];

    // TODO: 깔끔하게 스스로 종료하는 방법은?
	exit(exitCode);
}

- (void)getUUID:(id<AxPluginContext>)context {
    CFUUIDRef uuid = CFUUIDCreate(kCFAllocatorDefault);
    NSString *strUUID = [NSMakeCollectable(CFUUIDCreateString(kCFAllocatorDefault, uuid)) autorelease];
    CFRelease(uuid);
    [context sendResult:strUUID];
}

@end
