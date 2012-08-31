//
//  BuiltinDeviceStatusPlugin.java
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "BuiltinDeviceStatusPlugin.h"

#include <sys/types.h>
#include <sys/sysctl.h>

@implementation BuiltinDeviceStatusPlugin

- (void)getVendor:(DefaultPluginContext*)context {
    [context sendResult:@"Apple, Inc"];
}

- (void)getModel:(DefaultPluginContext*)context {
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *platform = [NSString stringWithUTF8String:machine];
    free(machine);
    [context sendResult:platform];
    return;
}

@end
