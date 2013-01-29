/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
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
