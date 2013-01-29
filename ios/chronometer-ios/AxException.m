/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
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
