/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

#import "AxException.h"


@implementation AxException

+(AxException*)exceptionWithMessage:(NSString*)message cause:(NSException*)cause {
    return [[[AxException alloc] initWithMessage:message cause:cause] autorelease];
}

-(id)initWithMessage:(NSString*)message cause:(NSException*)cause {
    return [super initWithName:@"AxException" reason:message userInfo:[NSDictionary dictionaryWithObject:cause forKey:@"cause"]];
}

-(NSString*)message {
    return self.reason;
}

-(NSException*)cause {
    return (NSException*)[self.userInfo objectForKey:@"cause"];
}

@end

@implementation AxException(AxExceptionRaisingConveniences)

+(void)raise {
    @throw [[AxException alloc] initWithMessage:@"Unknown Appspresso Exception" cause:nil];
}

+(void)raiseWithMessage:(NSString*)message {
    @throw [[AxException alloc] initWithMessage:message cause:nil];
}

+(void)raiseWithCause:(NSException*)cause {
    @throw [[AxException alloc] initWithMessage:@"Unknown Appspresso Exception" cause:cause];
}

+(void)raiseWithMessage:(NSString*)message cause:(NSException*)cause {
    @throw [[AxException alloc] initWithMessage:message cause:cause];
}

@end
