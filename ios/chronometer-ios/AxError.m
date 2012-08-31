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

#import "AxError.h"


@implementation AxError

/**
 * 사용자 정의 에러를 위한 유일한 에러 코드 생성.
 */
+(int)toErrorCode:(NSString*)errorId {
    return AX_CUSTOM_ERR_FLAG | [errorId hash];
}

+(AxError*)errorWithCode:(NSInteger)code message:(NSString*)message cause:(NSException*)cause {
    return [[[AxError alloc] initWithCode:code message:message cause:cause] autorelease];
}

-(id)initWithCode:(NSInteger)code message:(NSString*)message cause:(NSException*)cause {
    return [super initWithDomain:@"AxError" code:code userInfo:[NSDictionary dictionaryWithObjectsAndKeys:message, @"message", cause, @"cause", nil]];
}

-(NSString*)message {
    // TODO: dynamically translate code to message via lookup table?
    return (NSString*)[self.userInfo objectForKey:@"message"];
}

-(NSException*)cause {
    return (NSException*)[self.userInfo objectForKey:@"cause"];
}

@end
