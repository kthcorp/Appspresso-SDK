//
//  AxSessionKeyHolder.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxSessionKeyHolder.h"
#include <CommonCrypto/CommonDigest.h>
#include <stdlib.h>

@implementation AxSessionKeyHolder

- (id)init {
    if (self = [super init]) {
        _key = nil;
    }

    return self;
}

- (void)dealloc {
    [_key release];

    [super dealloc];
}

+ (AxSessionKeyHolder*)instance {
    static AxSessionKeyHolder *singleton = nil;

    if (singleton == nil) {
        singleton = [[AxSessionKeyHolder alloc] init];
    }

    return singleton;
}

- (NSString*)key {
    if (_key == nil) {
        return @"appspresso session uninitialized";
    }
    return _key;
}

- (NSString*)generate {
    if (_key != nil) {
        return _key;
    }

    NSString* base = [NSString stringWithFormat:@"appspresso%d%d", [[NSDate date] timeIntervalSince1970], arc4random()];
    unsigned char digest[CC_SHA1_DIGEST_LENGTH];
    NSData *stringBytes = [base dataUsingEncoding: NSUTF8StringEncoding];

    if (CC_SHA1([stringBytes bytes], [stringBytes length], digest)) {
        NSMutableString *output = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH];
        for (int i = 0; i < CC_SHA1_DIGEST_LENGTH; i++) {
            [output appendFormat:@"%02x", digest[i]];
        }
        _key = [output retain];
        return _key;
    }

    _key = [base retain];
    return _key;
}


@end
